package javaclasses;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;

public class Engine3D extends Thread {

	public int intWidth;
	public int intHeight;

	public JFrame jFrmWindow;

	public Canvas cnvsWindow;

	public boolean boolRunning = true;

	public int intOffsetX;
	public int intOffsetY;

	public PointD CamPos = new PointD(0.0F, 0.0F, 0.0F);
	public PointD CamRot = new PointD(0.0F, 0.0F, 0.0F);

	public float[][] float2DViewMatrix = { { 1.0F, 0.0F, 0.0F, 0.0F },
			{ 0.0F, 1.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 1.0F, 0.0F },
			{ 0.0F, 0.0F, 0.0F, 1.0F } };

	public float[][] float2DProjectionMatrix;

	public PointDArray Light;

	public boolean boolBackFaces = false;
	public boolean boolTriangleMesh = false;
	public boolean boolTriangleFaces = false;
	public boolean boolTrianglePoints = true;
	public boolean boolNames = false;
	public boolean boolZCulling = false;
	public boolean boolShading = false;
	public boolean boolDoAnimation = false;
	public boolean boolShowModelMatrix = false;
	public boolean boolShowViewMatrix = false;
	public boolean boolShowModelData = false;

	public int intBackgroundCol = getCol(0, 0, 0);
	public int intModelCol = getCol(0, 0, 255);
	public int intTextCol = getCol(255, 255, 0);

	private int[] intArrPixelData;

	private float[] floatArrZData;

	private GraphicsConfiguration GraphicsConfig = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice()
			.getDefaultConfiguration();

	private BufferedImage bffrdImgBlitData;
	private BufferStrategy bffrStrtgyBuffers;
	private Graphics2D g2D_DrawGraphics;

	private boolean boolBGROS;

	private int intMaxL;

	private long longATimer;
	private int intAPoint;
	private boolean boolTimerSet = false;

	public Engine3D(int int3DWidth, int int3DHeight) {

		intWidth = int3DWidth;
		intHeight = int3DHeight;

		Light = new PointDArray();
		Light.E.add(new PointD(0, 0, -10));

		setCamPos(0.0F, 0.0F, -10.0F);

		intMaxL = (int) Math.sqrt(Math.pow(intWidth, 2.0D)
				+ Math.pow(intHeight, 2.0D));

		intOffsetX = (int3DWidth / 2);
		intOffsetY = (int3DHeight / 2);

		float2DProjectionMatrix = new float[4][4];

		boolBGROS = ((System.getProperty("os.name").startsWith("Win")) || (System
				.getProperty("os.name").startsWith("Sol")));

		intArrPixelData = new int[intWidth * intHeight];

		floatArrZData = new float[intWidth * intHeight];

		jFrmWindow = new JFrame();
		jFrmWindow.setDefaultCloseOperation(3);
		jFrmWindow.setSize(intWidth + 6, intHeight + 28);
		jFrmWindow.setResizable(false);
		Dimension D = Toolkit.getDefaultToolkit().getScreenSize();
		jFrmWindow.setLocation(D.width / 24, D.height / 24);
		jFrmWindow.setTitle("Engine");
		System.setProperty("sun.java2d.translaccel", "true");
		System.setProperty("sun.java2d.ddforcevram", "true");

		cnvsWindow = new Canvas(GraphicsConfig);
		cnvsWindow.setSize(intWidth, intHeight);

		jFrmWindow.add(cnvsWindow, 0);

		bffrdImgBlitData = GraphicsConfig.createCompatibleImage(intWidth,
				intHeight);

		intArrPixelData = ((DataBufferInt) bffrdImgBlitData.getRaster()
				.getDataBuffer()).getData();

		drawString(new PointD(0.0F, 0.0F), "Lucida Console", " ", 24, 1, 255);

		jFrmWindow.setVisible(true);

		cnvsWindow.createBufferStrategy(3);

		changeProjectView(2.0F, -2.0F, -2.0F, 2.0F, 40.0F, 100.0F);
	}

	@Override
	public void run() {
		while (true)
			render();
	}

	public void render() {
		do {

			getGraphicsBuffer();
			g2D_DrawGraphics.drawImage(bffrdImgBlitData, 0, 0, null);

		} while (!updateScreen());
	}

	public float Dot(PointD PointD_1, PointD PointD_2) {
		return PointD_1.X() * PointD_2.X() + PointD_1.Y() * PointD_2.Y()
				+ PointD_1.Z() * PointD_2.Z();
	}

	public PointD Sub(PointD PointD_1, PointD PointD_2) {
		return new PointD(PointD_1.X() - PointD_2.X(), PointD_1.Y()
				- PointD_2.Y(), PointD_1.Z() - PointD_2.Z());
	}

	public PointD Cross(PointD PointD_1, PointD PointD_2) {
		return new PointD(PointD_1.Y() * PointD_2.Z() - PointD_1.Z()
				* PointD_2.Y(), PointD_1.Z() * PointD_2.X() - PointD_1.X()
				* PointD_2.Z(), PointD_1.X() * PointD_2.Y() - PointD_1.Y()
				* PointD_2.X());
	}

	public float Sign(PointD PointD_A, PointD PointD_B, PointD PointD_C) {
		return (PointD_A.X() - PointD_C.X()) * (PointD_B.Y() - PointD_C.Y())
				- (PointD_B.X() - PointD_C.X()) * (PointD_A.Y() - PointD_C.Y());
	}

	public boolean PointInTri(PointD PointD_P, PointD PointD_A,
			PointD PointD_B, PointD PointD_C) {

		boolean[] boolArrTemp = new boolean[3];

		boolArrTemp[0] = (Sign(PointD_P, PointD_A, PointD_B) <= 0.0F);
		boolArrTemp[1] = (Sign(PointD_P, PointD_B, PointD_C) <= 0.0F);
		boolArrTemp[2] = (Sign(PointD_P, PointD_C, PointD_A) <= 0.0F);

		return (boolArrTemp[0] == boolArrTemp[1])
				&& (boolArrTemp[1] == boolArrTemp[2]);
	}

	public void drawString(PointD PointD_P, String strFont, String strText,
			int intFontSize, int intFontStyle, int intCol) {

		if (strText == null)
			return;

		Font F = new Font(strFont, intFontStyle, intFontSize);

		@SuppressWarnings("serial")
		FontMetrics FM = new FontMetrics(F) {
		};

		int Offset = 0;
		if (intFontStyle == 2)
			Offset = 5;

		BufferedImage Temp = new BufferedImage((int) FM.getStringBounds(
				strText, null).getWidth()
				+ Offset, (int) FM.getStringBounds(strText, null).getHeight(),
				2);

		FM = null;

		int[] RGBVals = getRGB(intCol);

		Graphics2D G2DTemp = Temp.createGraphics();
		G2DTemp.setColor(new Color(RGBVals[0], RGBVals[1], RGBVals[2]));
		G2DTemp.setFont(F);

		F = null;

		G2DTemp.drawString(strText, 0, G2DTemp.getFontMetrics().getMaxAscent());
		G2DTemp.dispose();
		G2DTemp = null;

		for (int X = 0; X < Temp.getWidth(); X++)
			for (int Y = 0; Y < Temp.getHeight(); Y++) {
				if ((Temp.getRGB(X, Y) == 0)
						|| (!pixelOnScreen(new PointD(X + PointD_P.X(), Y
								+ PointD_P.Y()), false)))
					continue;
				intArrPixelData[(int) ((Y + PointD_P.Y()) * intWidth + (X + PointD_P
						.X()))] = intCol;
				floatArrZData[(int) ((Y + PointD_P.Y()) * intWidth + (X + PointD_P
						.X()))] = CamPos.Z();
			}
		Temp = null;
	}

	public void drawTriangle(PointD PointD_A, PointD PointD_B, PointD PointD_C,
			int intCol) {

		PointDArray[] PointDArr_Lines = new PointDArray[3];
		PointDArr_Lines[0] = joinPoints(PointD_A, PointD_B);
		PointDArr_Lines[1] = joinPoints(PointD_B, PointD_C);
		PointDArr_Lines[2] = joinPoints(PointD_C, PointD_A);
		int intArrIndex = 0;
		float floatZValue = (PointD_A.Z() + PointD_B.Z() + PointD_C.Z()) / 3.0F;
		PointD PointD_Temp = new PointD();

		for (int intCounter = 0; intCounter <= 2; intCounter++) {
			for (int intCounter2 = 0; intCounter2 < PointDArr_Lines[intCounter].E
					.size(); intCounter2++) {

				PointD_Temp.set(((PointD) PointDArr_Lines[intCounter].E
						.get(intCounter2)).X(),
						((PointD) PointDArr_Lines[intCounter].E
								.get(intCounter2)).Y(), floatZValue);

				if (pixelOnScreen(PointD_Temp, boolZCulling)) {

					if (boolShading)
						intCol = calcVecCol(PointD_Temp, Light);

					intArrIndex = (int) (((PointD) PointDArr_Lines[intCounter].E
							.get(intCounter2)).Y() * intWidth + ((PointD) PointDArr_Lines[intCounter].E
							.get(intCounter2)).X());

					intArrPixelData[intArrIndex] = intCol;
					floatArrZData[intArrIndex] = floatZValue;
				}
			}
		}

		PointDArr_Lines = null;
		PointD_A = null;
		PointD_B = null;
		PointD_C = null;
	}

	public void drawTriangleF(PointD PointD_A, PointD PointD_B,
			PointD PointD_C, int intCol, boolean boolColorInTri) {

		float[] floatTempX = { PointD_A.X(), PointD_B.X(), PointD_C.X() };
		float[] floatTempY = { PointD_A.Y(), PointD_B.Y(), PointD_C.Y() };
		floatTempX = ShellSortfloat(floatTempX);
		floatTempY = ShellSortfloat(floatTempY);
		int intW = (int) (floatTempX[2] - floatTempX[0]);
		int intH = (int) (floatTempY[2] - floatTempY[0]);
		float floatZValue = (PointD_A.Z() + PointD_B.Z() + PointD_C.Z()) / 3.0F;

		PointD PointD_Temp = new PointD();

		for (int intCounter = 0; intCounter < intW; intCounter++)
			for (int intCounter2 = 0; intCounter2 < intH; intCounter2++) {

				PointD_Temp.set(floatTempX[0] + intCounter, floatTempY[0]
						+ intCounter2, floatZValue);

				if ((!PointInTri(PointD_Temp, PointD_A, PointD_B, PointD_C))
						|| (!pixelOnScreen(PointD_Temp, boolZCulling)))
					continue;

				if (boolColorInTri)
					intArrPixelData[(int) (PointD_Temp.Y() * intWidth + PointD_Temp
							.X())] = intCol;
				floatArrZData[(int) (PointD_Temp.Y() * intWidth + PointD_Temp
						.X())] = PointD_Temp.Z();
			}
		floatTempX = null;
		floatTempY = null;
	}

	public PointD[] ShellSortP(PointD[] PointDArr, boolean boolX) {

		PointD PointD_Temp = new PointD();

		int intPivot = PointDArr.length / 2;
		int intJ;

		while (intPivot > 0) {

			for (int intCounter = intPivot; intCounter < PointDArr.length; intCounter++) {

				PointD_Temp.set(PointDArr[intCounter].X(),
						PointDArr[intCounter].Y());
				intJ = intCounter;

				if (boolX)
					while (PointDArr[(intJ - intPivot)].X() > PointD_Temp.X()) {
						PointDArr[intJ] = PointDArr[(intJ - intPivot)];
						intJ -= intPivot;
						if (intJ < intPivot)
							break;
					}
				else {

					while (PointDArr[(intJ - intPivot)].Y() > PointD_Temp.Y()) {
						PointDArr[intJ] = PointDArr[(intJ - intPivot)];
						intJ -= intPivot;
						if (intJ < intPivot)
							break;
					}
				}

				PointDArr[intJ].set(PointD_Temp.X(), PointD_Temp.Y());
			}

			intPivot /= 2;
		}
		return PointDArr;
	}

	public float[] ShellSortfloat(float[] floatArr) {

		int intPivot = floatArr.length / 2;

		float floatTemp;

		int intJ;

		while (intPivot > 0) {

			for (int intCounter = intPivot; intCounter < floatArr.length; intCounter++) {

				floatTemp = floatArr[intCounter];
				intJ = intCounter;

				while (floatArr[(intJ - intPivot)] > floatTemp) {
					floatArr[intJ] = floatArr[(intJ - intPivot)];
					intJ -= intPivot;
					if (intJ < intPivot)
						break;
				}

				floatArr[intJ] = floatTemp;
			}

			intPivot /= 2;
		}
		return floatArr;
	}

	public void drawBox(PointD PointD_1, PointD PointD_2, int intCol) {

		PointDArray[] PointDArr_Lines = new PointDArray[4];
		PointDArr_Lines[0] = joinPoints(PointD_1, new PointD(PointD_2.X(),
				PointD_1.Y()));
		PointDArr_Lines[1] = joinPoints(PointD_1, new PointD(PointD_1.X(),
				PointD_2.Y()));
		PointDArr_Lines[2] = joinPoints(PointD_2, new PointD(PointD_2.X(),
				PointD_1.Y()));
		PointDArr_Lines[3] = joinPoints(PointD_2, new PointD(PointD_1.X(),
				PointD_2.Y()));

		for (int intCounter = 0; intCounter <= 3; intCounter++) {
			for (int intCounter2 = 0; intCounter2 < PointDArr_Lines[intCounter].E
					.size(); intCounter2++) {
				intArrPixelData[(int) (((PointD) PointDArr_Lines[intCounter].E
						.get(intCounter2)).Y() * intWidth + ((PointD) PointDArr_Lines[intCounter].E
							.get(intCounter2)).X())] = intCol;
				floatArrZData[(int) (((PointD) PointDArr_Lines[intCounter].E
						.get(intCounter2)).Y() * intWidth + ((PointD) PointDArr_Lines[intCounter].E
							.get(intCounter2)).X())] = ((PointD) PointDArr_Lines[intCounter].E
						.get(intCounter2)).Z();
			}
		}

		PointDArr_Lines = null;
	}

	public void drawLine(PointD PointD_1, PointD PointD_2, int intCol) {

		PointDArray PointDArray_Line = joinPoints(PointD_1, PointD_2);

		for (int intCounter = 0; intCounter < PointDArray_Line.E.size(); intCounter++) {
			if (pixelOnScreen(PointDArray_Line.E.get(intCounter), boolZCulling)) {

				intArrPixelData[(int) (((PointD) PointDArray_Line.E
						.get(intCounter)).Y() * intWidth + ((PointD) PointDArray_Line.E
							.get(intCounter)).X())] = intCol;
				floatArrZData[(int) (((PointD) PointDArray_Line.E
						.get(intCounter)).Y() * intWidth + ((PointD) PointDArray_Line.E
							.get(intCounter)).X())] = ((PointD) PointDArray_Line.E
						.get(intCounter)).Z();
			}
		}
		PointDArray_Line = null;
	}

	public void drawPixel(PointD PointD_P, int intCol) {

		if (pixelOnScreen(PointD_P, boolZCulling)) {
			if (boolShading)
				intArrPixelData[(int) (PointD_P.Y() * intWidth + PointD_P.X())] = calcVecCol(
						PointD_P, Light);
			else
				intArrPixelData[(int) (PointD_P.Y() * intWidth + PointD_P.X())] = intCol;
			floatArrZData[(int) (PointD_P.Y() * intWidth + PointD_P.X())] = PointD_P
					.Z();
		}
	}

	public void clearScreen() {
		for (int intX = 0; intX < intWidth; intX++)
			for (int intY = 0; intY < intHeight; intY++) {
				intArrPixelData[(intY * intWidth + intX)] = intBackgroundCol;
				floatArrZData[(intY * intWidth + intX)] = 999999999;
			}
	}

	public float[][] multiplyMatrices(float[][] float2D_Matrix1,
			float[][] float2D_Matrix2) {

		float[][] float2DTemp = new float[4][4];

		for (int intX = 0; intX < 4; intX++)
			for (int intY = 0; intY < 4; intY++)
				for (int intZ = 0; intZ < 4; intZ++)
					float2DTemp[intX][intY] += float2D_Matrix2[intX][intZ]
							* float2D_Matrix1[intZ][intY];
		return float2DTemp;
	}

	public void changeProjectView(float floatUp, float floatDown,
			float floatLeft, float floatRight, float floatNear, float floatFar) {
		for (int intX = 0; intX <= 3; intX++)
			for (int intY = 0; intY <= 3; intY++)
				float2DProjectionMatrix[intX][intY] = 0.0F;

		float2DProjectionMatrix[0][0] = (2.0F * floatNear / (floatRight - floatLeft));
		float2DProjectionMatrix[0][2] = ((floatRight + floatLeft) / (floatRight - floatLeft));
		float2DProjectionMatrix[1][1] = (2.0F * floatNear / (floatUp - floatDown));
		float2DProjectionMatrix[1][2] = ((floatUp + floatDown) / (floatUp - floatDown));
		float2DProjectionMatrix[2][2] = (-(floatFar + floatNear) / (floatFar - floatNear));
		float2DProjectionMatrix[2][3] = (-(2.0F * floatFar * floatNear) / (floatFar - floatNear));
		float2DProjectionMatrix[3][2] = -1.0F;
	}

	public void resetViewMatrix() {
		for (int intX = 0; intX <= 3; intX++)
			for (int intY = 0; intY <= 3; intY++)
				float2DViewMatrix[intX][intY] = (intX == intY ? 1 : 0);
	}

	public PointD getAngles(float[][] float2DModelMatrix) {
		return new PointD(
				(float) (Math.atan2(float2DModelMatrix[2][1],
						float2DModelMatrix[2][2]) * (180 / Math.PI)),
				(float) (Math.atan2(
						-float2DModelMatrix[2][0],
						Math.sqrt(Math.pow(float2DModelMatrix[2][1], 2)
								+ Math.pow(float2DModelMatrix[2][2], 2))) * (180 / Math.PI)),
				(float) (Math.atan2(float2DModelMatrix[1][0],
						float2DModelMatrix[0][0]) * (180 / Math.PI)));
	}

	public PointD getPos(float[][] float2DModelMatrix) {
		return new PointD(float2DModelMatrix[0][3], float2DModelMatrix[1][3],
				float2DModelMatrix[2][3]);
	}

	public boolean[] Culling(TriangleArray3D TriArr3D_Triangles) { // culling
		boolean[] boolArrTemp = new boolean[TriArr3D_Triangles.E.length];
		for (int intCounter = 0; intCounter < boolArrTemp.length; intCounter++) {
			boolArrTemp[intCounter] = (Dot(
					Cross(Sub(TriArr3D_Triangles.E[intCounter].a,
							TriArr3D_Triangles.E[intCounter].b),
							Sub(TriArr3D_Triangles.E[intCounter].a,
									TriArr3D_Triangles.E[intCounter].c)),
					CamPos) > 0.0F);
		}
		return boolArrTemp;
	}

	public boolean Cull(Triangle3D Tri3D_Triangle) {
		return Dot(
				Cross(Sub(Tri3D_Triangle.a, Tri3D_Triangle.b),
						Sub(Tri3D_Triangle.a, Tri3D_Triangle.c)), CamPos) > 0.0F;
	}

	private int[] HSL_RGB(float floatH, float floatS, float floatL) {
		float floatQ = 0;
		if (floatL < 0.5)
			floatQ = floatL * (1 + floatS);
		else
			floatQ = (floatL + floatS) - (floatS * floatL);

		float floatP = 2 * floatL - floatQ;

		return new int[] {
				Math.round(255 * Math.max(0,
						Hue_RGB(floatP, floatQ, floatH + (1.0f / 3.0f)))),
				Math.round(255 * Math.max(0, Hue_RGB(floatP, floatQ, floatH))),
				Math.round(255 * Math.max(0,
						Hue_RGB(floatP, floatQ, floatH - (1.0f / 3.0f)))) };
	}

	private float Hue_RGB(float floatP, float floatQ, float floatH) {
		if (floatH < 0)
			floatH += 1;
		if (floatH > 1)
			floatH -= 1;
		if (6 * floatH < 1)
			return floatP + ((floatQ - floatP) * 6 * floatH);
		if (2 * floatH < 1)
			return floatQ;
		if (3 * floatH < 2) {
			return floatP + ((floatQ - floatP) * 6 * ((2.0f / 3.0f) - floatH));
		}
		return floatP;
	}

	private float[] RGB_HSL(int intR, int intG, int intB) { // rgb to hsl

		float floatR = intR / 255f;
		float floatG = intG / 255f;
		float floatB = intB / 255f;

		float floatMin = Math.min(floatR, Math.min(floatG, floatB));
		float floatMax = Math.max(floatR, Math.max(floatG, floatB));

		float floatH = 0;
		float floatS = 0;
		float floatL = (floatMax + floatMin) / 2;
		if (floatMin == floatMax)
			floatH = 0;
		else if (floatMax == floatR)
			floatH = (((floatG - floatB) / (floatMax - floatMin) / 6f) + 1) % 1;
		else if (floatMax == floatG)
			floatH = ((floatB - floatR) / (floatMax - floatMin) / 6f) + 1f / 3f;
		else if (floatMax == floatB)
			floatH = ((floatR - floatG) / (floatMax - floatMin) / 6f) + 2f / 3f;
		if (floatMin == floatMax)
			floatS = 0;
		else if (floatL <= 0.5f)
			floatS = (floatMax - floatMin) / (floatMax + floatMin);
		else
			floatS = (floatMax - floatMin) / (2 - floatMax - floatMin);
		if (floatS > 1)
			floatS = 1;
		if (floatS < 0)
			floatS = 0;
		if (floatL > 1)
			floatL = 1;
		if (floatL < 0)
			floatL = 0;
		return new float[] { floatH, floatS, floatL };
	}

	public int calcVecCol(PointD PointD_P, PointDArray PointDArr_Light) {

		float floatLum = 0.0F;
		for (int intCounter = 0; intCounter < PointDArr_Light.E.size(); intCounter++) {
			floatLum += Dot(PointDArr_Light.E.get(intCounter), PointD_P);
		}

		int[] intArrRGB = getRGB(intModelCol);
		float[] floatArrHSL = new float[3];

		floatArrHSL = RGB_HSL(intArrRGB[0], intArrRGB[1], intArrRGB[2]);

		intArrRGB = HSL_RGB(floatArrHSL[0], floatArrHSL[1], floatLum / 100);

		return getCol(intArrRGB[0], intArrRGB[1], intArrRGB[2]);
	}

	public int calcFaceCol(Triangle3D Tri3D_Triangles,
			PointDArray PointDArr_Light) { // calcualte face col

		float floatLum = 0.0F;
		for (int intCounter = 0; intCounter < PointDArr_Light.E.size(); intCounter++) {

			floatLum += Dot(
					PointDArr_Light.E.get(intCounter),
					new PointD(
							(Tri3D_Triangles.a.X() + Tri3D_Triangles.b.X() + Tri3D_Triangles.c
									.X()) / 3.0F, (Tri3D_Triangles.a.Y()
									+ Tri3D_Triangles.b.Y() + Tri3D_Triangles.c
									.Y()) / 3.0F, (Tri3D_Triangles.a.Z()
									+ Tri3D_Triangles.b.Z() + Tri3D_Triangles.c
									.Z()) / 3.0F));
		}

		int[] intArrRGB = getRGB(intModelCol);
		float[] floatArrHSL = new float[3];
		floatArrHSL = RGB_HSL(intArrRGB[0], intArrRGB[1], intArrRGB[2]);

		intArrRGB = HSL_RGB(floatArrHSL[0], floatArrHSL[1], floatLum / 100);

		return getCol(intArrRGB[0], intArrRGB[1], intArrRGB[2]);
	}

	public float[][] returnAnimationMatrix(Object3D Obj3D_Obj,
			int intAnimation, int intAnimationNo) {

		float[][] float2DTempM = { { 1.0F, 0.0F, 0.0F, 0.0F },
				{ 0.0F, 1.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 1.0F, 0.0F },
				{ 0.0F, 0.0F, 0.0F, 1.0F } };

		if (Obj3D_Obj.Animations == null)
			return float2DTempM;

		float2DTempM[0][0] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16));
		float2DTempM[0][1] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 1);
		float2DTempM[0][2] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 2);
		float2DTempM[0][3] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 3);
		float2DTempM[1][0] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 4);
		float2DTempM[1][1] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 5);
		float2DTempM[1][2] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 6);
		float2DTempM[1][3] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 7);
		float2DTempM[2][0] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 8);
		float2DTempM[2][1] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 9);
		float2DTempM[2][2] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 10);
		float2DTempM[2][3] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 11);
		float2DTempM[3][0] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 12);
		float2DTempM[3][1] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 13);
		float2DTempM[3][2] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 14);
		float2DTempM[3][2] = Obj3D_Obj.Animations[intAnimation].matrices
				.get((intAnimationNo * 16) + 15);

		return float2DTempM;

	}

	public void drawObject(Object3D Obj3D_Obj) {

		if (Obj3D_Obj.Triangles == null)
			return;

		resetViewMatrix();

		changeProjectView(Obj3D_Obj.Size / 2, -Obj3D_Obj.Size / 2,
				-Obj3D_Obj.Size / 2, Obj3D_Obj.Size / 2, 20.0F, 80.0F);

		float2DViewMatrix = rotateCameraMatrix(float2DViewMatrix, -CamRot.X(),
				-CamRot.Y(), -CamRot.Z());
		float2DViewMatrix = translateCameraMatrix(float2DViewMatrix,
				-CamPos.X(), -CamPos.Y(), -CamPos.Z());

		if (boolDoAnimation && Obj3D_Obj.Animate) {

			float[][] float2DTempM = returnAnimationMatrix(Obj3D_Obj, 0,
					intAPoint);

			PointD PointD_TempPos = getPos(float2DTempM);
			PointD PointD_TempRot = getAngles(float2DTempM);

			Obj3D_Obj.ModelMatrix = translateModelMatrix(Obj3D_Obj.ModelMatrix,
					PointD_TempPos.X(), PointD_TempPos.Z(), PointD_TempPos.Y());
			Obj3D_Obj.ModelMatrix = translateModelMatrix(Obj3D_Obj.ModelMatrix,
					Obj3D_Obj.ModelPos.X(), Obj3D_Obj.ModelPos.Y(),
					Obj3D_Obj.ModelPos.Z());
			Obj3D_Obj.ModelMatrix = rotateModelMatrix(Obj3D_Obj.ModelMatrix,
					Obj3D_Obj.ModelRot.X(), Obj3D_Obj.ModelRot.Y(),
					Obj3D_Obj.ModelRot.Z());
			Obj3D_Obj.ModelMatrix = rotateModelMatrix(Obj3D_Obj.ModelMatrix,
					PointD_TempRot.X(), PointD_TempRot.Y(), PointD_TempRot.Z());

			if (!boolTimerSet) {
				longATimer = System.nanoTime();
				boolTimerSet = true;
			}

			try {
				if ((System.nanoTime() - longATimer) >= (Obj3D_Obj.Animations[0].time
						.get(intAPoint) * 10000000)) {
					boolTimerSet = false;
					if ((Obj3D_Obj.Animations[0].time.size() - 1) == intAPoint)
						intAPoint = 0;
					else
						intAPoint++;
				}
			} catch (Exception e) {

			}

		} else {

			Obj3D_Obj.ModelMatrix = translateModelMatrix(Obj3D_Obj.ModelMatrix,
					Obj3D_Obj.ModelPos.X(), Obj3D_Obj.ModelPos.Y(),
					Obj3D_Obj.ModelPos.Z());
			Obj3D_Obj.ModelMatrix = rotateModelMatrix(Obj3D_Obj.ModelMatrix,
					Obj3D_Obj.ModelRot.X(), Obj3D_Obj.ModelRot.Y(),
					Obj3D_Obj.ModelRot.Z());
		}

		Obj3D_Obj.ModelViewMatrix = multiplyMatrices(Obj3D_Obj.ModelMatrix,
				float2DViewMatrix);

		projectObject(Obj3D_Obj, Obj3D_Obj.ModelViewMatrix,
				float2DProjectionMatrix, true);

		for (int intCounter = 0; intCounter < Obj3D_Obj.Triangles.E.length; intCounter++) {

			if ((!boolBackFaces)
					| ((boolBackFaces) && (Obj3D_Obj.Cull[intCounter]))) {

				if (boolTriangleMesh) {
					drawTriangle(Obj3D_Obj.Triangles.E[intCounter].a,
							Obj3D_Obj.Triangles.E[intCounter].b,
							Obj3D_Obj.Triangles.E[intCounter].c, intModelCol);
				}

				if ((boolTriangleFaces) && (!boolShading)) {
					drawTriangleF(Obj3D_Obj.Triangles.E[intCounter].a,
							Obj3D_Obj.Triangles.E[intCounter].b,
							Obj3D_Obj.Triangles.E[intCounter].c, intModelCol,
							true);
				}

				if (boolTrianglePoints) {
					drawPixel(Obj3D_Obj.Triangles.E[intCounter].a, intModelCol);
					drawPixel(Obj3D_Obj.Triangles.E[intCounter].b, intModelCol);
					drawPixel(Obj3D_Obj.Triangles.E[intCounter].c, intModelCol);
				}

				if ((boolTriangleFaces) && (boolShading)) {
					drawTriangleF(
							Obj3D_Obj.Triangles.E[intCounter].a,
							Obj3D_Obj.Triangles.E[intCounter].b,
							Obj3D_Obj.Triangles.E[intCounter].c,
							calcFaceCol(Obj3D_Obj.Triangles.E[intCounter],
									Light), true);
				}
			}
		}

		if (boolNames)
			drawString(Obj3D_Obj.Triangles.E[0].a, "Lucida Console",
					Obj3D_Obj.Name, 12, Font.PLAIN, intTextCol);

		if (boolShowModelData) {
			drawString(new PointD(intWidth * 0.5, (intHeight * 0.75) + 20),
					"Lucida Console", "Vertices : " + Obj3D_Obj.noVerts, 12,
					Font.PLAIN, intTextCol);
			drawString(new PointD(intWidth * 0.5, (intHeight * 0.75) + 40),
					"Lucida Console", "Triangles : "
							+ Obj3D_Obj.Triangles.E.length, 12, Font.PLAIN,
					intTextCol);
			drawString(new PointD(intWidth * 0.5, (intHeight * 0.75) + 60),
					"Lucida Console",
					"Model Position : " + Obj3D_Obj.ModelPos.toString(), 12,
					Font.PLAIN, intTextCol);
			drawString(new PointD(intWidth * 0.5, (intHeight * 0.75) + 80),
					"Lucida Console",
					"Model Rotation : " + Obj3D_Obj.ModelRot.toString(), 12,
					Font.PLAIN, intTextCol);
		}

		if (boolShowModelMatrix) {
			drawString(new PointD(20, (int) (intHeight * 0.75)),
					"Lucida Console", "Model Matrix", 12, Font.PLAIN,
					intTextCol);
			drawString(new PointD(20, (int) (intHeight * 0.75) + 20),
					"Lucida Console", "[" + Obj3D_Obj.ModelMatrix[0][0] + ", "
							+ Obj3D_Obj.ModelMatrix[0][1] + ", "
							+ Obj3D_Obj.ModelMatrix[0][2] + ", "
							+ Obj3D_Obj.ModelMatrix[0][3] + "]", 12,
					Font.PLAIN, intTextCol);
			drawString(new PointD(20, (int) (intHeight * 0.75) + 40),
					"Lucida Console", "[" + Obj3D_Obj.ModelMatrix[1][0] + ", "
							+ Obj3D_Obj.ModelMatrix[1][1] + ", "
							+ Obj3D_Obj.ModelMatrix[1][2] + ", "
							+ Obj3D_Obj.ModelMatrix[1][3] + "]", 12,
					Font.PLAIN, intTextCol);
			drawString(new PointD(20, (int) (intHeight * 0.75) + 60),
					"Lucida Console", "[" + Obj3D_Obj.ModelMatrix[2][0] + ", "
							+ Obj3D_Obj.ModelMatrix[2][1] + ", "
							+ Obj3D_Obj.ModelMatrix[2][2] + ", "
							+ Obj3D_Obj.ModelMatrix[2][3] + "]", 12,
					Font.PLAIN, intTextCol);
			drawString(new PointD(20, (int) (intHeight * 0.75) + 80),
					"Lucida Console", "[" + Obj3D_Obj.ModelMatrix[3][0] + ", "
							+ Obj3D_Obj.ModelMatrix[3][1] + ", "
							+ Obj3D_Obj.ModelMatrix[3][2] + ", "
							+ Obj3D_Obj.ModelMatrix[3][3] + "]", 12,
					Font.PLAIN, intTextCol);
		}

		if (boolShowViewMatrix) {
			drawString(new PointD((int) (intWidth * 0.60), 20),
					"Lucida Console", "View Matrix", 12, Font.PLAIN, intTextCol);
			drawString(new PointD((int) (intWidth * 0.60), 40),
					"Lucida Console", "[" + float2DViewMatrix[0][0] + ", "
							+ float2DViewMatrix[0][1] + ", "
							+ float2DViewMatrix[0][2] + ", "
							+ float2DViewMatrix[0][3] + "]", 12, Font.PLAIN,
					intTextCol);
			drawString(new PointD((int) (intWidth * 0.60), 60),
					"Lucida Console", "[" + float2DViewMatrix[1][0] + ", "
							+ float2DViewMatrix[1][1] + ", "
							+ float2DViewMatrix[1][2] + ", "
							+ float2DViewMatrix[1][3] + "]", 12, Font.PLAIN,
					intTextCol);
			drawString(new PointD((int) (intWidth * 0.60), 80),
					"Lucida Console", "[" + float2DViewMatrix[2][0] + ", "
							+ float2DViewMatrix[2][1] + ", "
							+ float2DViewMatrix[2][2] + ", "
							+ float2DViewMatrix[2][3] + "]", 12, Font.PLAIN,
					intTextCol);
			drawString(new PointD((int) (intWidth * 0.60), 100),
					"Lucida Console", "[" + float2DViewMatrix[3][0] + ", "
							+ float2DViewMatrix[3][1] + ", "
							+ float2DViewMatrix[3][2] + ", "
							+ float2DViewMatrix[3][3] + "]", 12, Font.PLAIN,
					intTextCol);
		}

		Obj3D_Obj.resetAll();

	}

	public PointD projectPointD(PointD PointD_P,
			float[][] float2DModelViewMatrix, float[][] float2DProjectionMatrix) {

		float[] floatArrTransModelView = new float[4];

		float[] floatArrTransProjection = new float[3];

		for (int intCounter = 0; intCounter < 4; intCounter++) {
			floatArrTransModelView[intCounter] = (float2DModelViewMatrix[intCounter][0]
					* PointD_P.X()
					+ float2DModelViewMatrix[intCounter][1]
					* PointD_P.Y()
					+ float2DModelViewMatrix[intCounter][2]
					* PointD_P.Z() + float2DModelViewMatrix[intCounter][3]);
		}

		for (int intCounter = 0; intCounter < 3; intCounter++)
			floatArrTransProjection[intCounter] = (float2DProjectionMatrix[intCounter][0]
					* floatArrTransModelView[0]
					+ float2DProjectionMatrix[intCounter][1]
					* floatArrTransModelView[1]
					+ float2DProjectionMatrix[intCounter][2]
					* floatArrTransModelView[2] + float2DProjectionMatrix[intCounter][3]
					* floatArrTransModelView[3]);

		if (floatArrTransModelView[2] == 0.0F)
			return new PointD(PointD_P.X(), PointD_P.Y(), 0.0F);

		for (int intCounter = 0; intCounter < 3; intCounter++) {
			float[] floatArrTemp = floatArrTransProjection;
			floatArrTemp[intCounter] = (float) (floatArrTemp[intCounter] * (1.0D / floatArrTransModelView[2]));
		}

		PointD_P = null;
		floatArrTransModelView = null;

		return normalizePoint(new PointD(floatArrTransProjection[0] * intMaxL,
				floatArrTransProjection[1] * intMaxL,
				floatArrTransProjection[2]));
	}

	public void projectObject(Object3D Obj3D_Obj,
			float[][] float2DModelViewMatrix,
			float[][] float2DProjectionMatrix, boolean boolNormalize) {

		float[] floatArrTransModelView = new float[4];

		float[] floatArrTransProjection = new float[3];

		Triangle3D Tri3D_Temp = new Triangle3D();

		for (int intCounter = 0; intCounter < Obj3D_Obj.Triangles.E.length; intCounter++) {

			for (int intCounter2 = 0; intCounter2 < 3; intCounter2++) {

				for (int intCounter3 = 0; intCounter3 <= 3; intCounter3++) {
					switch (intCounter2) {
					case 0:
						floatArrTransModelView[intCounter3] = (float2DModelViewMatrix[intCounter3][0]
								* Obj3D_Obj.Triangles.E[intCounter].a.X()
								+ float2DModelViewMatrix[intCounter3][1]
								* Obj3D_Obj.Triangles.E[intCounter].a.Y()
								+ float2DModelViewMatrix[intCounter3][2]
								* Obj3D_Obj.Triangles.E[intCounter].a.Z() + float2DModelViewMatrix[intCounter3][3]);
						break;
					case 1:
						floatArrTransModelView[intCounter3] = (float2DModelViewMatrix[intCounter3][0]
								* Obj3D_Obj.Triangles.E[intCounter].b.X()
								+ float2DModelViewMatrix[intCounter3][1]
								* Obj3D_Obj.Triangles.E[intCounter].b.Y()
								+ float2DModelViewMatrix[intCounter3][2]
								* Obj3D_Obj.Triangles.E[intCounter].b.Z() + float2DModelViewMatrix[intCounter3][3]);
						break;
					case 2:
						floatArrTransModelView[intCounter3] = (float2DModelViewMatrix[intCounter3][0]
								* Obj3D_Obj.Triangles.E[intCounter].c.X()
								+ float2DModelViewMatrix[intCounter3][1]
								* Obj3D_Obj.Triangles.E[intCounter].c.Y()
								+ float2DModelViewMatrix[intCounter3][2]
								* Obj3D_Obj.Triangles.E[intCounter].c.Z() + float2DModelViewMatrix[intCounter3][3]);
						break;
					}
				}

				for (int intCounter3 = 0; intCounter3 < 3; intCounter3++)
					floatArrTransProjection[intCounter3] = (float2DProjectionMatrix[intCounter3][0]
							* floatArrTransModelView[0]
							+ float2DProjectionMatrix[intCounter3][1]
							* floatArrTransModelView[1]
							+ float2DProjectionMatrix[intCounter3][2]
							* floatArrTransModelView[2] + float2DProjectionMatrix[intCounter3][3]
							* floatArrTransModelView[3]);

				if (floatArrTransModelView[2] == 0.0F) {
					switch (intCounter2) {
					case 0:
						Tri3D_Temp.a.set(
								Obj3D_Obj.Triangles.E[intCounter].a.X(),
								Obj3D_Obj.Triangles.E[intCounter].a.Y(), 0.0F);
						break;
					case 1:
						Tri3D_Temp.b.set(
								Obj3D_Obj.Triangles.E[intCounter].b.X(),
								Obj3D_Obj.Triangles.E[intCounter].b.Y(), 0.0F);
						break;
					case 2:
						Tri3D_Temp.c.set(
								Obj3D_Obj.Triangles.E[intCounter].c.X(),
								Obj3D_Obj.Triangles.E[intCounter].c.Y(), 0.0F);
						break;
					default:
						break;
					}
				} else {

					for (int intCounter3 = 0; intCounter3 < 3; intCounter3++) {
						float[] floatArrTemp = floatArrTransProjection;
						floatArrTemp[intCounter3] = (float) (floatArrTemp[intCounter3] * (1.0D / floatArrTransModelView[2]));
					}

					switch (intCounter2) {
					case 0:
						Tri3D_Temp.a.set(floatArrTransProjection[0] * intMaxL,
								floatArrTransProjection[1] * intMaxL,
								floatArrTransProjection[2]);
						break;
					case 1:
						Tri3D_Temp.b.set(floatArrTransProjection[0] * intMaxL,
								floatArrTransProjection[1] * intMaxL,
								floatArrTransProjection[2]);
						break;
					case 2:
						Tri3D_Temp.c.set(floatArrTransProjection[0] * intMaxL,
								floatArrTransProjection[1] * intMaxL,
								floatArrTransProjection[2]);
						break;
					}
				}
			}

			if (boolBackFaces)
				Obj3D_Obj.Cull[intCounter] = Cull(Tri3D_Temp);

			if (boolNormalize) {
				Obj3D_Obj.Triangles.E[intCounter].a = normalizePoint(Tri3D_Temp.a);
				Obj3D_Obj.Triangles.E[intCounter].b = normalizePoint(Tri3D_Temp.b);
				Obj3D_Obj.Triangles.E[intCounter].c = normalizePoint(Tri3D_Temp.c);
			} else {

				Obj3D_Obj.Triangles.E[intCounter] = Tri3D_Temp;
			}
		}
	}

	public float[][] rotateModelMatrix(float[][] float2DMatrix, float floatX,
			float floatY, float floatZ) {
		return rotateMatrixZ(
				rotateMatrixY(rotateMatrixX(float2DMatrix, floatX), floatY),
				floatZ);
	}

	public float[][] translateModelMatrix(float[][] float2DMatrix,
			float floatX, float floatY, float floatZ) {

		float[][] float2DTemp = { { 1.0F, 0.0F, 0.0F, floatX },
				{ 0.0F, 1.0F, 0.0F, floatY }, { 0.0F, 0.0F, 1.0F, floatZ },
				{ 0.0F, 0.0F, 0.0F, 1.0F } };

		return multiplyMatrices(float2DTemp, float2DMatrix);
	}

	public float[][] rotateCameraMatrix(float[][] float2DMatrix, float floatX,
			float floatY, float floatZ) {
		return rotateMatrixX(
				rotateMatrixY(rotateMatrixZ(float2DMatrix, floatZ), floatY),
				floatX);
	}

	public float[][] translateCameraMatrix(float[][] float2DMatrix,
			float floatX, float floatY, float floatZ) {

		float[][] float2DTemp = { { 1.0F, 0.0F, 0.0F, floatX },
				{ 0.0F, 1.0F, 0.0F, floatY }, { 0.0F, 0.0F, 1.0F, floatZ },
				{ 0.0F, 0.0F, 0.0F, 1.0F } };

		return multiplyMatrices(float2DTemp, float2DMatrix);
	}

	private boolean updateScreen() {

		if (g2D_DrawGraphics != null) {
			g2D_DrawGraphics.dispose();
			g2D_DrawGraphics = null;
		}

		bffrStrtgyBuffers.show();
		Toolkit.getDefaultToolkit().sync();
		return !bffrStrtgyBuffers.contentsLost();
	}

	public PointDArray joinPoints(PointD PointD_P1, PointD PointD_P2) {

		PointD PointD_Temp = new PointD();
		PointDArray PointDArr_Temp = new PointDArray();

		if ((PointD_P1.X() == PointD_P2.X())
				&& (PointD_P1.Y() == PointD_P2.Y())) {
			if (pixelOnScreen(PointD_P1, false))
				PointDArr_Temp.E.add(PointD_P1);
			return PointDArr_Temp;
		}

		if (PointD_P1.X() == PointD_P2.X()) {

			int intPoints = (int) (Math.abs(PointD_P1.Y() - PointD_P2.Y()) + 1.0D);

			if (PointD_P1.Y() < PointD_P2.Y()) {
				for (int intCounter = 0; intCounter < intPoints; intCounter++) {

					PointD_Temp = new PointD(PointD_P1.X(), PointD_P1.Y()
							+ intCounter);

					if (pixelOnScreen(PointD_Temp, false))
						PointDArr_Temp.E.add(PointD_Temp);
				}

				return PointDArr_Temp;
			} else {

				for (int intCounter = 0; intCounter < intPoints; intCounter++) {

					PointD_Temp = new PointD(PointD_P1.X(), PointD_P1.Y()
							- intCounter);

					if (pixelOnScreen(PointD_Temp, false))
						PointDArr_Temp.E.add(PointD_Temp);
				}

				return PointDArr_Temp;
			}

		}

		if (PointD_P1.Y() == PointD_P2.Y()) {

			int intPoints = (int) (Math.abs(PointD_P1.X() - PointD_P2.X()) + 1.0D);

			if (PointD_P1.X() < PointD_P2.X()) {
				for (int intCounter = 0; intCounter < intPoints; intCounter++) {

					PointD_Temp = new PointD(PointD_P1.X() + intCounter,
							PointD_P1.Y());

					if (pixelOnScreen(PointD_Temp, false))
						PointDArr_Temp.E.add(PointD_Temp);
				}

				return PointDArr_Temp;
			} else {

				for (int intCounter = 0; intCounter < intPoints; intCounter++) {

					PointD_Temp = new PointD(PointD_P1.X() - intCounter,
							PointD_P1.Y());

					if (pixelOnScreen(PointD_Temp, false))
						PointDArr_Temp.E.add(PointD_Temp);
				}

				return PointDArr_Temp;
			}

		}

		double doubleGradient = (PointD_P2.Y() - PointD_P1.Y())
				/ (PointD_P2.X() - PointD_P1.X());

		if (Math.abs(PointD_P1.X() - PointD_P2.X()) < Math.abs(PointD_P1.Y()
				- PointD_P2.Y())) {

			if (PointD_P2.Y() < PointD_P1.Y()) {
				PointD II = new PointD(PointD_P1.X(), PointD_P1.Y());
				PointD_P1 = new PointD(PointD_P2.X(), PointD_P2.Y());
				PointD_P2 = new PointD(II.X(), II.Y());
			}

			double doubleYIntercept = PointD_P1.Y() - doubleGradient
					* PointD_P1.X();

			int intPoints = (int) (PointD_P2.Y() - PointD_P1.Y() + 1.0D);

			for (int intCounter = 0; intCounter < intPoints; intCounter++) {

				PointD_Temp = new PointD(
						(int) ((PointD_P1.Y() + intCounter - doubleYIntercept) / doubleGradient),
						PointD_P1.Y() + intCounter);

				if (pixelOnScreen(PointD_Temp, false))
					PointDArr_Temp.E.add(PointD_Temp);
			}

			return PointDArr_Temp;
		}

		if (Math.abs(PointD_P1.X() - PointD_P2.X()) >= Math.abs(PointD_P1.Y()
				- PointD_P2.Y())) {

			if (PointD_P2.X() < PointD_P1.X()) {
				PointD II = new PointD(PointD_P1.X(), PointD_P1.Y());
				PointD_P1 = new PointD(PointD_P2.X(), PointD_P2.Y());
				PointD_P2 = new PointD(II.X(), II.Y());
			}

			double doubleYIntercept = PointD_P1.Y() - doubleGradient
					* PointD_P1.X();

			int intPoints = (int) (PointD_P2.X() - PointD_P1.X() + 1.0D);

			for (int intCounter = 0; intCounter < intPoints; intCounter++) {

				PointD_Temp = new PointD(
						PointD_P1.X() + intCounter,
						(int) (doubleGradient * (PointD_P1.X() + intCounter) + doubleYIntercept));

				if (pixelOnScreen(PointD_Temp, false))
					PointDArr_Temp.E.add(PointD_Temp);
			}

			return PointDArr_Temp;
		}

		if ((PointD_P2.Y() - PointD_P1.Y()) / (PointD_P2.X() - PointD_P1.X()) == 1.0D) {

			if (PointD_P2.X() < PointD_P1.X()) {
				PointD II = new PointD(PointD_P1.X(), PointD_P1.Y());
				PointD_P1 = new PointD(PointD_P2.X(), PointD_P2.Y());
				PointD_P2 = new PointD(II.X(), II.Y());
			}

			int intPoints = (int) (PointD_P2.X() - PointD_P1.X() + 1.0D);

			for (int intCounter = 0; intCounter < intPoints; intCounter++) {

				PointD_Temp = new PointD(PointD_P1.X() + intCounter,
						PointD_P1.Y() + intCounter);

				if (pixelOnScreen(PointD_Temp, false))
					PointDArr_Temp.E.add(PointD_Temp);
			}

			return PointDArr_Temp;
		}

		return PointDArr_Temp;
	}

	private void getGraphicsBuffer() {
		if (g2D_DrawGraphics == null)
			try {
				bffrStrtgyBuffers = cnvsWindow.getBufferStrategy();
				g2D_DrawGraphics = ((Graphics2D) bffrStrtgyBuffers
						.getDrawGraphics());
			} catch (IllegalStateException e) {
				System.out.println(e.getMessage());
			}
	}

	private boolean pixelOnScreen(PointD PointD_P, boolean boolZCull) {
		if (boolZCull) {
			return (PointD_P.X() >= 0.0F)

					&& (PointD_P.X() < intWidth)

					&& (PointD_P.Y() >= 0.0F)

					&& (PointD_P.Y() < intHeight)

					&& (PointD_P.Z() < floatArrZData[(int) (PointD_P.Y()
							* intWidth + PointD_P.X())])

					&& (PointD_P.Z() > CamPos.Z())

			;
		} else

			return (PointD_P.X() >= 0.0F) && (PointD_P.X() < intWidth)
					&& (PointD_P.Y() >= 0.0F) && (PointD_P.Y() < intHeight);
	}

	public int getCol(int intR, int intG, int intB) {
		return !boolBGROS ? intR << 16 | intG << 8 | intB : intB << 16
				| intG << 8 | intR;
	}

	public int[] getRGB(int intCol) {
		if (boolBGROS)
			return new int[] { intCol % 256, intCol / 256 % 256,
					intCol / 65536 % 256 };
		else
			return new int[] { intCol / 65536 % 256, intCol / 256 % 256,
					intCol % 256 };
	}

	public float[][] rotateMatrixX(float[][] float2DMatrix, double doubleAngle) {

		double doubleRadians = doubleAngle * (Math.PI / 180);

		float[][] float2DTemp = {
				{ 1.0F, 0.0F, 0.0F, 0.0F },
				{ 0.0F, (float) Math.cos(doubleRadians),
						(float) (-Math.sin(doubleRadians)), 0.0F },
				{ 0.0F, (float) Math.sin(doubleRadians),
						(float) Math.cos(doubleRadians), 0.0F },
				{ 0.0F, 0.0F, 0.0F, 1.0F } };

		return multiplyMatrices(float2DTemp, float2DMatrix);
	}

	public float[][] rotateMatrixY(float[][] float2DMatrix, double doubleAngle) {

		double doubleRadians = doubleAngle * (Math.PI / 180);

		float[][] float2DTemp = {
				{ (float) Math.cos(doubleRadians), 0.0F,
						(float) Math.sin(doubleRadians), 0.0F },
				{ 0.0F, 1.0F, 0.0F, 0.0F },
				{ (float) (-Math.sin(doubleRadians)), 0.0F,
						(float) Math.cos(doubleRadians), 0.0F },
				{ 0.0F, 0.0F, 0.0F, 1.0F } };

		return multiplyMatrices(float2DTemp, float2DMatrix);
	}

	public float[][] rotateMatrixZ(float[][] float2DMatrix, double doubleAngle) {
		double doubleRadians = doubleAngle * (Math.PI / 180);
		float[][] float2DTemp = {
				{ (float) Math.cos(doubleRadians),
						(float) (-Math.sin(doubleRadians)), 0.0F, 0.0F },
				{ (float) Math.sin(doubleRadians),
						(float) Math.cos(doubleRadians), 0.0F, 0.0F },
				{ 0.0F, 0.0F, 1.0F, 0.0F }, { 0.0F, 0.0F, 0.0F, 1.0F } };
		return multiplyMatrices(float2DTemp, float2DMatrix);
	}

	public void setCamPosX(float floatX) {
		CamPos.setX(floatX);
	}

	public void setCamPosY(float floatY) {
		CamPos.setY(floatY);
	}

	public void setCamPosZ(float floatZ) {
		CamPos.setZ(floatZ);

	}

	public void setCamPos(float floatX, float floatY, float floatZ) {
		setCamPosX(floatX);
		setCamPosY(floatY);
		setCamPosZ(floatZ);
	}

	public void setCamRotX(float floatX) {
		CamRot.setX(floatX);
	}

	public void setCamRotY(float floatY) {
		CamRot.setY(floatY);
	}

	public void setCamRotZ(float floatZ) {
		CamRot.setZ(floatZ);
	}

	public PointD normalizePoint(PointD PointD_P) {

		if (PointD_P.Z() == 0.0F)
			return new PointD(Math.round(PointD_P.X() + intOffsetX),
					Math.round(PointD_P.Y() + intOffsetY), 0.0F);
		else

			return new PointD((int) Math.round(PointD_P.X()
					* (1.0D / PointD_P.Z()) + intOffsetX),
					(int) Math.round(PointD_P.Y() * (1.0D / PointD_P.Z())
							+ intOffsetY), PointD_P.Z());
	}

}
