package javaclasses;

import java.io.FileInputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Object3D {

	public TriangleArray3D Triangles;

	public PointDArray Vertices;

	public PointDArray Normals;

	public Animation[] Animations;

	public float[][] TexCoords;

	public float[][] ModelMatrix = { { 1.0F, 0.0F, 0.0F, 0.0F },
			{ 0.0F, 1.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 1.0F, 0.0F },
			{ 0.0F, 0.0F, 0.0F, 1.0F } };

	public float[][] ModelViewMatrix;

	public boolean[] Cull;

	public PointD ModelPos = new PointD(0.0F, 0.0F, 0.0F);

	public PointD ModelRot = new PointD(0.0F, 0.0F, 0.0F);

	public String Name;

	public boolean Animate = false;

	public float Size;

	public int noVerts;

	private int TrL;

	private int VeL;

	private boolean TCs;

	private IndexedTriangleArray Ae;

	public Object3D(String filePath) {
		try {

			if (filePath == "")
				return;

			String[] P = { "COLLADA", "library_geometries", "geometry", "mesh" };
			int[] P2 = { 0, 0, 0, 0 };
			this.TCs = HasTextures(getMainNode(filePath), P, P2);
			P = null;
			P2 = null;

			this.ModelViewMatrix = new float[4][4];
			this.Name = filePath;

			String[] P3 = { "COLLADA", "library_geometries", "geometry",
					"mesh", "source", "float_array" };
			int[] P4 = { 0, 0, 0, 0, 0, 0 };
			ArrayList<Float> Vals = returnSourceContent_F(
					getMainNode(filePath), P3, P4);
			P3 = null;
			P4 = null;
			int A = Vals.size();
			if ((((A / 3) % 1) != 0) || (A / 3 < 1))
				throw new Exception("Invalid/Corrupt DAE File!");

			float[] getSize = new float[A];
			for (int I = 0; I < A; I++)
				getSize[I] = ((Float) Vals.get(I)).floatValue();
			getSize = ShellSortfloat(getSize);
			Size = getSize[getSize.length - 1] - getSize[0];
			getSize = null;

			this.Vertices = new PointDArray();
			noVerts = A;
			for (int T = 0; T < A; T += 3)
				this.Vertices.E.add(new PointD(((Float) Vals.get(T))
						.floatValue(), ((Float) Vals.get(T + 1)).floatValue(),
						((Float) Vals.get(T + 2)).floatValue()));
			Vals = null;

			String[] P5 = { "COLLADA", "library_geometries", "geometry",
					"mesh", "triangles", "p" };
			int[] P6 = { 0, 0, 0, 0, 0, 0 };
			ArrayList<Integer> Vals2 = returnSourceContent_I(
					getMainNode(filePath), P5, P6);
			P5 = null;
			P6 = null;

			if (this.TCs) {
				this.Ae = new IndexedTriangleArray(Vals2.size() / 9);
				A = 0;
				for (int I = 0; I < Vals2.size() / 9; I++) {
					this.Ae.E[I].Vertices.E.add((PointD) this.Vertices.E
							.get(((Integer) Vals2.get(A))));
					this.Ae.E[I].Vertices.E.add((PointD) this.Vertices.E
							.get(((Integer) Vals2.get(A + 3))));
					this.Ae.E[I].Vertices.E.add((PointD) this.Vertices.E
							.get(((Integer) Vals2.get(A + 6))));
					A += 9;
				}
				this.VeL = (this.Ae.E.length * 3);
				this.TrL = this.Ae.E.length;
			} else {
				this.Ae = new IndexedTriangleArray(Vals2.size() / 6);
				A = 0;
				for (int I = 0; I < Vals2.size() / 6; I++) {
					this.Ae.E[I].Vertices.E.add((PointD) this.Vertices.E
							.get(((Integer) Vals2.get(A)).intValue()));
					this.Ae.E[I].Vertices.E.add((PointD) this.Vertices.E
							.get(((Integer) Vals2.get(A + 2)).intValue()));
					this.Ae.E[I].Vertices.E.add((PointD) this.Vertices.E
							.get(((Integer) Vals2.get(A + 4)).intValue()));
					A += 6;
				}
				this.TrL = this.Ae.E.length;
				this.VeL = (this.Ae.E.length * 3);
			}
			Vals2 = null;
			resetTriangles();
			this.Cull = new boolean[this.TrL];
			Vals = null;

			String[] PS = { "COLLADA", "library_animations" };
			int[] PSS = { 0, 0 };

			if (HasAnimations(getMainNode(filePath), PS, PSS)) {
				Animate = true;
				Animations = new Animation[1];
				Animations[0] = new Animation();
				String[] P7 = { "COLLADA", "library_animations", "animation",
						"animation", "source", "float_array" };
				int[] P8 = { 0, 0, 0, 0, 0, 0 };

				Vals = returnSourceContent_F(getMainNode(filePath), P7, P8);

				P7 = null;
				P8 = null;

				for (int I = 0; I < Vals.size(); I++) {
					Animations[0].time.add(Vals.get(I));
				}

				String[] P9 = { "COLLADA", "library_animations", "animation",
						"animation", "source", "float_array" };
				int[] P10 = { 0, 0, 0, 0, 1, 0 };
				Vals = returnSourceContent_F(getMainNode(filePath), P9, P10);

				P9 = null;
				P10 = null;
				for (int I = 0; I < Vals.size(); I += 16) {
					Animations[0].matrices.add(Vals.get(I));
					Animations[0].matrices.add(Vals.get(I + 1));
					Animations[0].matrices.add(Vals.get(I + 2));
					Animations[0].matrices.add(Vals.get(I + 3));

					Animations[0].matrices.add(Vals.get(I + 8));
					Animations[0].matrices.add(Vals.get(I + 9));
					Animations[0].matrices.add(Vals.get(I + 10));
					Animations[0].matrices.add(Vals.get(I + 11));

					Animations[0].matrices.add(Vals.get(I + 4));
					Animations[0].matrices.add(Vals.get(I + 5));
					Animations[0].matrices.add(Vals.get(I + 6));
					Animations[0].matrices.add(Vals.get(I + 7));

					Animations[0].matrices.add(Vals.get(I + 12));
					Animations[0].matrices.add(Vals.get(I + 13));
					Animations[0].matrices.add(Vals.get(I + 14));
					Animations[0].matrices.add(Vals.get(I + 15));

				}

				Vals = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Invalid Data Supplied!");

		}
	}

	public void resetTriangles() {
		this.Triangles = null;
		this.Triangles = new TriangleArray3D(this.TrL);
		for (int I = 0; I < this.Triangles.E.length; I++) {
			this.Triangles.E[I].a = ((PointD) this.Ae.E[I].Vertices.E.get(0));
			this.Triangles.E[I].b = ((PointD) this.Ae.E[I].Vertices.E.get(1));
			this.Triangles.E[I].c = ((PointD) this.Ae.E[I].Vertices.E.get(2));
		}
	}

	public void resetNormals() {
		this.Normals = null;
		this.Normals = new PointDArray(this.TrL);
	}

	public void resetVertices() {
		this.Vertices = null;
		this.Vertices = new PointDArray(this.VeL);
		for (int I = 0; I < this.VeL / 3; I += 3) {
			this.Vertices.E.add((PointD) this.Ae.E[I].Vertices.E.get(0));
			this.Vertices.E.add((PointD) this.Ae.E[I].Vertices.E.get(1));
			this.Vertices.E.add((PointD) this.Ae.E[I].Vertices.E.get(2));
		}
	}

	public void resetModelMatrix() {

		for (int X = 0; X <= 3; X++)
			for (int Y = 0; Y <= 3; Y++)

				if (X == Y)
					this.ModelMatrix[X][Y] = 1.0F;
				else
					this.ModelMatrix[X][Y] = 0.0F;
	}

	public void resetAll() {
		resetNormals();
		resetVertices();
		resetTriangles();
		resetModelMatrix();
	}

	public void setXPos(float X) {
		this.ModelPos.setX(X);
	}

	public void setYPos(float Y) {
		this.ModelPos.setY(Y);
	}

	public void setZPos(float Z) {
		this.ModelPos.setZ(Z);
	}

	public void setPos(float X, float Y, float Z) {
		this.ModelPos.set(X, Y, Z);
	}

	public void shiftXPos(float X) {
		this.ModelPos.setX(this.ModelPos.X() + X);
	}

	public void shiftYPos(float Y) {
		this.ModelPos.setY(this.ModelPos.Y() + Y);
	}

	public void shiftZPos(float Z) {
		this.ModelPos.setZ(this.ModelPos.Z() + Z);
	}

	public void setXRot(float X) {
		this.ModelRot.setX(X);
	}

	public void setYRot(float Y) {
		this.ModelRot.setY(Y);
	}

	public void setZRot(float Z) {
		this.ModelRot.setZ(Z);
	}

	public void setRot(float X, float Y, float Z) {
		this.ModelRot.set(X, Y, Z);
	}

	public void shiftXRot(float X) {
		this.ModelRot.setX(this.ModelRot.X() + X);
	}

	public void shiftYRot(float Y) {
		this.ModelRot.setY(this.ModelRot.Y() + Y);
	}

	public void shiftZRot(float Z) {
		this.ModelRot.setZ(this.ModelRot.Z() + Z);
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

	private static boolean isFloat(String S) {
		try {

			Float.parseFloat(S);

			return true;
		} catch (NumberFormatException e) {

			return false;
		}
	}

	private static boolean isInt(String S) {
		try {

			Integer.parseInt(S);

			return true;
		} catch (NumberFormatException e) {

			return false;
		}
	}

	public static ArrayList<Float> returnSourceContent_F(Node ParentNode,
			String[] StrPath, int[] intPath) {

		if (StrPath.length != intPath.length)
			return null;

		Node TempNode = ParentNode;

		for (int I = 0; I < StrPath.length; I++)
			TempNode = returnNodeN(TempNode, StrPath[I], intPath[I]);

		String[] StrArrTemp = TempNode.getTextContent().split("\\s+");
		ArrayList<Float> Result = new ArrayList<Float>();

		for (int I = 0; I < StrArrTemp.length; I++)
			if (isFloat(StrArrTemp[I]))
				Result.add(Float.valueOf(Float.parseFloat(StrArrTemp[I])));

		return Result;
	}

	public static ArrayList<Integer> returnSourceContent_I(Node ParentNode,
			String[] StrPath, int[] intPath) {

		if (StrPath.length != intPath.length)
			return null;

		Node TempNode = ParentNode;

		for (int I = 0; I < StrPath.length; I++)
			TempNode = returnNodeN(TempNode, StrPath[I], intPath[I]);

		String[] StrArrTemp = TempNode.getTextContent().split("\\s+");
		ArrayList<Integer> Result = new ArrayList<Integer>();

		for (int I = 0; I < StrArrTemp.length; I++)
			if (isInt(StrArrTemp[I]))
				Result.add(Integer.valueOf(Integer.parseInt(StrArrTemp[I])));

		return Result;
	}

	public static Node returnNode(Node ParentNode, String P) {

		for (int I = 0; I < ParentNode.getChildNodes().getLength(); I++)
			if (ParentNode.getChildNodes().item(I).getNodeName().compareTo(P) == 0)

				return ParentNode.getChildNodes().item(I);

		return ParentNode;
	}

	public static Node returnNodeN(Node ParentNode, String P, int Which) {
		int W = Which;

		for (int I = 0; I < ParentNode.getChildNodes().getLength(); I++)
			if (ParentNode.getChildNodes().item(I).getNodeName().compareTo(P) == 0)
				if (W == 0)

					return ParentNode.getChildNodes().item(I);
				else

					W = W - 1;

		return ParentNode;
	}

	public static int returnSameNodes_Length(Node ParentNode, String P) {
		int C = 0;

		for (int I = 0; I < ParentNode.getChildNodes().getLength(); I++)
			if (ParentNode.getChildNodes().item(I).getNodeName().compareTo(P) == 0)
				C++;
		return C;
	}

	public static boolean HasTextures(Node ParentNode, String[] StrPath,
			int[] intPath) {

		if (StrPath.length != intPath.length)
			return false;

		Node TempNode = ParentNode;

		for (int I = 0; I < StrPath.length; I++)
			TempNode = returnNodeN(TempNode, StrPath[I], intPath[I]);

		return (returnSameNodes_Length(TempNode, "source") == 3);
	}

	public static boolean HasAnimations(Node ParentNode, String[] StrPath,
			int[] intPath) {

		if (StrPath.length != intPath.length)
			return false;

		Node TempNode = ParentNode;

		for (int I = 0; I < StrPath.length - 1; I++)
			TempNode = returnNodeN(TempNode, StrPath[I], intPath[I]);

		return !(TempNode == returnNodeN(TempNode, StrPath[StrPath.length - 1],
				intPath[intPath.length - 1]));
	}

	public static Node getMainNode(String fileName) {
		Document DAEFile = null;
		try {

			DAEFile = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new FileInputStream(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return DAEFile.getChildNodes().item(0);
	}

}
