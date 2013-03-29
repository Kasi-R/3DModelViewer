package javaclasses;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main {

	private static Engine3D eng3D_Engine;

	private static JFrame jFrme_ControlFrame;

	private static JMenuBar jMnuBr_Menu;

	private static JMenu jMnu_File;

	private static JMenu jMnu_Edit;

	private static JMenu jMnu_Color;

	private static JMenuItem jMnuItm_ModelColor;

	private static JMenuItem jMnuItm_TextColor;

	private static JMenuItem jMnuItm_BackGroundColor;

	private static JMenuItem jMnuItm_Open;

	private static JMenuItem jMnuItm_Close;

	private static JMenuItem jMnuItm_Attachment;

	private static JMenuItem jMnuItm_Resize;

	private static ActionListener actnLstnr_Open;

	private static ActionListener actnLstnr_Close;

	private static ActionListener actnLstnr_Attachment;

	private static ActionListener actnLstnr_Resize;

	private static ActionListener actnLstnr_ModelColor;

	private static ActionListener actnLstnr_TextColor;

	private static ActionListener actnLstnr_BackGroundColor;

	private static JCheckBox jChkbox_BackfaceCulling;

	private static JCheckBox jChkbox_TriangleMesh;

	private static JCheckBox jChkbox_TriangleFace;

	private static JCheckBox jChkbox_ObjectNames;

	private static JCheckBox jChkbox_ZCulling;

	private static JCheckBox jChkbox_TrianglePoints;

	private static JCheckBox jChkbox_TriangleShading;

	private static JCheckBox jChkbox_Animation;

	private static JCheckBox jChkbox_ViewModelMatrix;

	private static JCheckBox jChkbox_ViewCameraMatrix;

	private static JCheckBox jChkbox_ViewModelData;

	private static ActionListener actnLstnr_BackfaceCulling;

	private static ActionListener actnLstnr_TriangleMesh;

	private static ActionListener actnLstnr_TriangleFace;

	private static ActionListener actnLstnr_ObjectNames;

	private static ActionListener actnLstnr_ZCulling;

	private static ActionListener actnLstnr_TrianglePoints;

	private static ActionListener actnLstnr_TriangleShading;

	private static ActionListener actnLstnr_Animation;

	private static ActionListener actnLstnr_ViewModelMatrix;

	private static ActionListener actnLstnr_ViewCameraMatrix;

	private static ActionListener actnLstnr_ViewModelData;

	private static JSlider jSldr_ModelRotX;

	private static JSlider jSldr_ModelRotY;

	private static JSlider jSldr_ModelRotZ;

	private static ChangeListener chngLstn_ModelRotX;

	private static ChangeListener chngLstn_ModelRotY;

	private static ChangeListener chngLstn_ModelRotZ;

	private static JTextField jTxtFld_ModelPosX;

	private static JTextField jTxtFld_ModelPosY;

	private static JTextField jTxtFld_ModelPosZ;

	private static ActionListener actnLstnr_ModelPosX;

	private static ActionListener actnLstnr_ModelPosY;

	private static ActionListener actnLstnr_ModelPosZ;

	private static JTextField jTxtFld_CameraRotX;

	private static JTextField jTxtFld_CameraRotY;

	private static JTextField jTxtFld_CameraRotZ;

	private static ActionListener actnLstnr_CameraRotX;

	private static ActionListener actnLstnr_CameraRotY;

	private static ActionListener actnLstnr_CameraRotZ;

	private static JTextField jTxtFld_CameraPosX;

	private static JTextField jTxtFld_CameraPosY;

	private static JTextField jTxtFld_CameraPosZ;

	private static ActionListener actnLstnr_CameraPosX;

	private static ActionListener actnLstnr_CameraPosY;

	private static ActionListener actnLstnr_CameraPosZ;

	public static MouseMotionListener msMnLstnr_Mouse;
	public static MouseWheelListener msWlLstnr_Mouse;
	public static MouseListener msLstnr_Mouse;

	private static double tDistance = 0;

	private static boolean MDown;

	private static int Mx;

	private static int My;

	private static boolean bool_Attachment = true;

	private static long F = 0L;

	private static long FPS;

	private static long TimeRunning;

	public static Object3D Object3D_Model = new Object3D("");

	private static Thread LogicThread;

	public static void main(String[] args) {

		if (args.length == 2)
			eng3D_Engine = new Engine3D(Integer.parseInt(args[0]),
					Integer.parseInt(args[1]));
		else

			eng3D_Engine = new Engine3D(640, 480);

		initFrame();

		initModelTools();

		initDrawOptions();

		jFrme_ControlFrame.setVisible(true);

		Runnable LogicLoop = new Runnable() {
			public void run() {
				do
					logicLoop();
				while (eng3D_Engine.boolRunning);
			}
		};

		LogicThread = new Thread(LogicLoop);
		getValues();
		LogicThread.setPriority(Thread.NORM_PRIORITY);
		LogicThread.start();

		TimeRunning = System.currentTimeMillis();
	}

	private static void initFrame() {

		jFrme_ControlFrame = new JFrame();
		jFrme_ControlFrame.setDefaultCloseOperation(3);
		jFrme_ControlFrame.setSize(300, eng3D_Engine.intHeight + 28);
		jFrme_ControlFrame.setResizable(false);
		jFrme_ControlFrame.setLocation(eng3D_Engine.jFrmWindow.getLocation().x
				+ eng3D_Engine.intWidth + 16,
				eng3D_Engine.jFrmWindow.getLocation().y);
		jFrme_ControlFrame.setTitle("Control Panel");
		jFrme_ControlFrame.setLayout(null);

		msLstnr_Mouse = new MouseListener() {

			@Override
			public void mousePressed(MouseEvent e) {

				MDown = true;
				Mx = e.getX();
				My = e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				MDown = false;
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

		};

		msMnLstnr_Mouse = new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {

				if (!MDown)
					return;

				double Angle = 0;
				double Distance = 0;

				try {

					Angle = Math.atan2((e.getY() - My), (e.getX() - Mx))
							* (180 / Math.PI);

					Distance = Math.sqrt(Math.pow((Mx - e.getX()), 2)
							+ Math.pow((My - e.getY()), 2));

					if ((Angle >= 60) && (Angle <= 120)
							&& (Distance > tDistance)) {
						Object3D_Model.ModelRot.setX((Object3D_Model.ModelRot
								.X() + 1.0f));
						jSldr_ModelRotX.setValue(Math
								.round(Object3D_Model.ModelRot.X()));
						tDistance = Distance;
					} else if ((Angle >= 60) && (Angle <= 120)
							&& (Distance < tDistance)) {

						Object3D_Model.ModelRot.setX((Object3D_Model.ModelRot
								.X() - 1.0f));
						jSldr_ModelRotX.setValue(Math
								.round(Object3D_Model.ModelRot.X()));
						tDistance = Distance;
					}
					if ((Angle <= -60) && (Angle >= -120)
							&& (Distance > tDistance)) {

						Object3D_Model.ModelRot.setX((Object3D_Model.ModelRot
								.X() - 1.0f));
						jSldr_ModelRotX.setValue(Math
								.round(Object3D_Model.ModelRot.X()));
						tDistance = Distance;
					} else if ((Angle <= -60) && (Angle >= -120)
							&& (Distance < tDistance)) {

						Object3D_Model.ModelRot.setX((Object3D_Model.ModelRot
								.X() + 1.0f));
						jSldr_ModelRotX.setValue(Math
								.round(Object3D_Model.ModelRot.X()));
						tDistance = Distance;
					}
					if ((Angle >= -30) && (Angle <= 30)
							&& (Distance > tDistance)) {

						Object3D_Model.ModelRot.setY((Object3D_Model.ModelRot
								.Y() - 1.0f));
						jSldr_ModelRotY.setValue(Math
								.round(Object3D_Model.ModelRot.Y()));
						tDistance = Distance;
					} else if ((Angle >= -30) && (Angle <= 30)
							&& (Distance < tDistance)) {

						Object3D_Model.ModelRot.setY((Object3D_Model.ModelRot
								.Y() + 1.0f));
						jSldr_ModelRotY.setValue(Math
								.round(Object3D_Model.ModelRot.Y()));
						tDistance = Distance;
					}
					if (((Angle <= -150) && (Angle >= -180) && (Distance > tDistance))
							|| ((Angle >= 150) && (Angle <= 180) && (Distance > tDistance))) {

						Object3D_Model.ModelRot.setY((Object3D_Model.ModelRot
								.Y() + 1.0f));
						jSldr_ModelRotY.setValue(Math
								.round(Object3D_Model.ModelRot.Y()));
						tDistance = Distance;
					} else if (((Angle <= -150) && (Angle >= -180) && (Distance < tDistance))
							|| ((Angle >= 150) && (Angle <= 180) && (Distance < tDistance))) {

						Object3D_Model.ModelRot.setY((Object3D_Model.ModelRot
								.Y() - 1.0f));
						jSldr_ModelRotY.setValue(Math
								.round(Object3D_Model.ModelRot.Y()));
						tDistance = Distance;

					}

				} catch (Exception E) {

				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}
		};

		msWlLstnr_Mouse = new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {

				int depthMoved = arg0.getWheelRotation();

				int Step = 0;
				int[] Check = { 20, 50, 100, 200, 500, 1000, 999999999 };
				int[] Steps = { 1, 3, 5, 10, 20, 50, 100 };

				for (int I = 0; I < Check.length; I++) {
					if (Object3D_Model.ModelPos.Z() >= 0) {
						if (Object3D_Model.ModelPos.Z() < Check[I]) {
							Step = Steps[I];
							break;
						}
					} else {
						if (Object3D_Model.ModelPos.Z() > -Check[I]) {
							Step = Steps[I];
							break;
						}
					}
				}

				if (depthMoved >= 0) {
					float float_ModelPosZ = Object3D_Model.ModelPos.Z() + Step;

					if ((float_ModelPosZ - (Object3D_Model.Size / 2)) < (eng3D_Engine.CamPos
							.Z() - 9)) {
						Object3D_Model.setZPos((eng3D_Engine.CamPos.Z() - 9)
								+ (Object3D_Model.Size / 2));
						getValues();
						return;
					}
					Object3D_Model.setZPos(checkValue(float_ModelPosZ));
					getValues();
				} else {

					if ((Object3D_Model.ModelPos.Z() - (Object3D_Model.Size / 2)) < (eng3D_Engine.CamPos
							.Z() - 9))
						return;

					if (Object3D_Model.ModelPos.Z() > 0)
						Object3D_Model.setZPos(Object3D_Model.ModelPos.Z()
								- Step);
					else
						Object3D_Model.setZPos(Object3D_Model.ModelPos.Z() - 1);
				}

				jTxtFld_ModelPosZ.setText(Float
						.toString(Object3D_Model.ModelPos.Z()));
			}
		};

		eng3D_Engine.jFrmWindow.addMouseWheelListener(msWlLstnr_Mouse);
		eng3D_Engine.cnvsWindow.addMouseMotionListener(msMnLstnr_Mouse);
		eng3D_Engine.cnvsWindow.addMouseListener(msLstnr_Mouse);

		eng3D_Engine.jFrmWindow.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent arg0) {

				if (bool_Attachment)
					jFrme_ControlFrame.setState(eng3D_Engine.jFrmWindow
							.getState());
			}

			public void componentShown(ComponentEvent arg0) {

				if (bool_Attachment)
					jFrme_ControlFrame.setState(eng3D_Engine.jFrmWindow
							.getState());
			}

			public void componentResized(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent arg0) {

				if (bool_Attachment) {
					jFrme_ControlFrame.setLocation(
							eng3D_Engine.jFrmWindow.getLocation().x
									+ eng3D_Engine.intWidth + 16,
							eng3D_Engine.jFrmWindow.getLocation().y);
					jFrme_ControlFrame.setState(eng3D_Engine.jFrmWindow
							.getState());
					if (!jFrme_ControlFrame.isShowing())
						jFrme_ControlFrame.toFront();
				}
			}
		});

		jFrme_ControlFrame.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				if (bool_Attachment)
					eng3D_Engine.jFrmWindow.setState(e.getNewState());
			}
		});

		jFrme_ControlFrame.addComponentListener(new ComponentListener() {

			public void componentHidden(ComponentEvent arg0) {
				if (bool_Attachment)
					eng3D_Engine.jFrmWindow.setState(jFrme_ControlFrame
							.getState());
			}

			public void componentShown(ComponentEvent arg0) {
				if (bool_Attachment)
					eng3D_Engine.jFrmWindow.setState(jFrme_ControlFrame
							.getState());
			}

			public void componentResized(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent arg0) {
				if (bool_Attachment) {
					eng3D_Engine.jFrmWindow.setLocation(
							jFrme_ControlFrame.getLocation().x
									- eng3D_Engine.intWidth - 16,
							jFrme_ControlFrame.getLocation().y);
					eng3D_Engine.jFrmWindow.setState(jFrme_ControlFrame
							.getState());
					if (!eng3D_Engine.jFrmWindow.isShowing())
						eng3D_Engine.jFrmWindow.toFront();
				}
			}
		});

		eng3D_Engine.jFrmWindow
				.addWindowStateListener(new WindowStateListener() {
					public void windowStateChanged(WindowEvent e) {
						if (bool_Attachment)
							jFrme_ControlFrame.setState(e.getNewState());
					}
				});

		jMnuBr_Menu = new JMenuBar();

		jMnu_File = new JMenu("File");
		jMnu_File.setMnemonic(70);

		jMnuItm_Open = new JMenuItem("Open");
		jMnuItm_Open.setMnemonic(79);
		jMnuItm_Open.setAccelerator(KeyStroke.getKeyStroke(79, 2));

		final JFileChooser jFlChsr_Object = new JFileChooser();
		actnLstnr_Open = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (jFlChsr_Object.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					Object3D_Model = new Object3D(jFlChsr_Object
							.getSelectedFile().getAbsolutePath());
					Runtime.getRuntime().gc();
				}
			}
		};
		jMnuItm_Open.addActionListener(actnLstnr_Open);

		jMnuItm_Close = new JMenuItem("Close");
		jMnuItm_Close.setMnemonic(67);
		jMnuItm_Close.setAccelerator(KeyStroke.getKeyStroke(87, 2));

		actnLstnr_Close = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Toolkit.getDefaultToolkit().getSystemEventQueue()
						.postEvent(new WindowEvent(jFrme_ControlFrame, 201));
			}
		};
		jMnuItm_Close.addActionListener(actnLstnr_Close);

		jMnu_File.add(jMnuItm_Open);
		jMnu_File.add(jMnuItm_Close);
		
		jMnu_Edit = new JMenu("Edit");
		jMnu_Edit.setMnemonic(69);

		jMnuItm_Attachment = new JMenuItem("Attachment");
		jMnuItm_Attachment.setMnemonic(65);
		jMnuItm_Attachment.setAccelerator(KeyStroke.getKeyStroke(65, 2));

		actnLstnr_Attachment = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (bool_Attachment) {
					bool_Attachment = false;
				} else {

					bool_Attachment = true;
					Dimension D = Toolkit.getDefaultToolkit().getScreenSize();
					eng3D_Engine.jFrmWindow.setLocation(D.width / 24,
							D.height / 24);
					jFrme_ControlFrame.setLocation(
							eng3D_Engine.jFrmWindow.getLocation().x
									+ eng3D_Engine.intWidth + 16,
							eng3D_Engine.jFrmWindow.getLocation().y);
					eng3D_Engine.jFrmWindow.setState(jFrme_ControlFrame
							.getState());
				}
			}
		};
		jMnuItm_Attachment.addActionListener(actnLstnr_Attachment);

		jMnuItm_Resize = new JMenuItem("Resize");
		jMnuItm_Resize.setMnemonic(82);
		jMnuItm_Resize.setAccelerator(KeyStroke.getKeyStroke(82, 2));

		actnLstnr_Resize = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JTextField jTxtFld_Width = new JTextField(6);
				JTextField jTxtFld_Height = new JTextField(6);

				JPanel jPnl_NewSize = new JPanel();
				jPnl_NewSize.add(new JLabel("Width "));
				jPnl_NewSize.add(jTxtFld_Width);
				jPnl_NewSize.add(Box.createHorizontalStrut(20));
				jPnl_NewSize.add(new JLabel("Height "));
				jPnl_NewSize.add(jTxtFld_Height);

				int int_Result = JOptionPane
						.showConfirmDialog(null, jPnl_NewSize,
								"Please Enter Width and Height Values", 2);

				if (int_Result == 0)
					try {

						int int_Width = Integer.parseInt(jTxtFld_Width
								.getText());
						int int_Height = Integer.parseInt(jTxtFld_Height
								.getText());

						eng3D_Engine.jFrmWindow.setDefaultCloseOperation(2);

						int intTempModelColor = eng3D_Engine.intModelCol;
						int intTempTextColor = eng3D_Engine.intTextCol;
						int intTempBackgroundColor = eng3D_Engine.intBackgroundCol;

						Toolkit.getDefaultToolkit()
								.getSystemEventQueue()
								.postEvent(
										new WindowEvent(
												eng3D_Engine.jFrmWindow, 201));

						boolean[] tempSelected = new boolean[11];
						tempSelected[0] = jChkbox_Animation.isSelected();
						tempSelected[1] = jChkbox_BackfaceCulling.isSelected();
						tempSelected[2] = jChkbox_TriangleMesh.isSelected();
						tempSelected[3] = jChkbox_TriangleFace.isSelected();
						tempSelected[4] = jChkbox_ObjectNames.isSelected();
						tempSelected[5] = jChkbox_ZCulling.isSelected();
						tempSelected[6] = jChkbox_TrianglePoints.isSelected();
						tempSelected[7] = jChkbox_TriangleShading.isSelected();
						tempSelected[8] = jChkbox_ViewModelMatrix.isSelected();
						tempSelected[9] = jChkbox_ViewCameraMatrix.isSelected();
						tempSelected[10] = jChkbox_ViewModelData.isSelected();

						eng3D_Engine = new Engine3D(int_Width, int_Height);

						eng3D_Engine.intModelCol = intTempModelColor;
						eng3D_Engine.intTextCol = intTempTextColor;
						eng3D_Engine.intBackgroundCol = intTempBackgroundColor;

						eng3D_Engine.boolDoAnimation = tempSelected[0];
						eng3D_Engine.boolBackFaces = tempSelected[1];
						eng3D_Engine.boolTriangleMesh = tempSelected[2];
						eng3D_Engine.boolTriangleFaces = tempSelected[3];
						eng3D_Engine.boolNames = tempSelected[4];
						eng3D_Engine.boolZCulling = tempSelected[5];
						eng3D_Engine.boolTrianglePoints = tempSelected[6];
						eng3D_Engine.boolShading = tempSelected[7];
						eng3D_Engine.boolShowModelMatrix = tempSelected[8];
						eng3D_Engine.boolShowViewMatrix = tempSelected[9];
						eng3D_Engine.boolShowModelData = tempSelected[10];

						eng3D_Engine.jFrmWindow
								.addMouseWheelListener(msWlLstnr_Mouse);
						eng3D_Engine.cnvsWindow
								.addMouseMotionListener(msMnLstnr_Mouse);
						eng3D_Engine.cnvsWindow.addMouseListener(msLstnr_Mouse);

						if (bool_Attachment) {
							eng3D_Engine.jFrmWindow.setLocation(
									jFrme_ControlFrame.getLocation().x
											- eng3D_Engine.intWidth - 16,
									jFrme_ControlFrame.getLocation().y);
							eng3D_Engine.jFrmWindow.setState(jFrme_ControlFrame
									.getState());
							if (!eng3D_Engine.jFrmWindow.isShowing())
								eng3D_Engine.jFrmWindow.toFront();
						}

						eng3D_Engine.jFrmWindow
								.addComponentListener(new ComponentListener() {
									public void componentHidden(
											ComponentEvent arg0) {

										if (bool_Attachment)
											jFrme_ControlFrame
													.setState(eng3D_Engine.jFrmWindow
															.getState());
									}

									public void componentShown(
											ComponentEvent arg0) {

										if (bool_Attachment)
											jFrme_ControlFrame
													.setState(eng3D_Engine.jFrmWindow
															.getState());
									}

									public void componentResized(
											ComponentEvent e) {
									}

									public void componentMoved(
											ComponentEvent arg0) {

										if (bool_Attachment) {
											jFrme_ControlFrame.setLocation(
													eng3D_Engine.jFrmWindow
															.getLocation().x
															+ eng3D_Engine.intWidth
															+ 16,
													eng3D_Engine.jFrmWindow
															.getLocation().y);
											jFrme_ControlFrame
													.setState(eng3D_Engine.jFrmWindow
															.getState());
											if (!jFrme_ControlFrame.isShowing())
												jFrme_ControlFrame.toFront();
										}
									}
								});

						eng3D_Engine.jFrmWindow
								.addWindowStateListener(new WindowStateListener() {
									public void windowStateChanged(WindowEvent e) {
										if (bool_Attachment)
											jFrme_ControlFrame.setState(e
													.getNewState());
									}
								});
					} catch (Exception E) {

						JOptionPane.showMessageDialog(null,
								"Invalid Data Supplied!");
					}
			}
		};
		jMnuItm_Resize.addActionListener(actnLstnr_Resize);
		jMnu_Edit.add(jMnuItm_Attachment);
		jMnu_Edit.add(jMnuItm_Resize);

		jMnu_Color = new JMenu("Color");
		jMnu_Color.setMnemonic(67);

		jMnuItm_ModelColor = new JMenuItem("Model Color");
		jMnuItm_ModelColor.setMnemonic(77);
		jMnuItm_ModelColor.setAccelerator(KeyStroke.getKeyStroke(77, 2));

		actnLstnr_ModelColor = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				int[] Cols = eng3D_Engine.getRGB(eng3D_Engine.intModelCol);
				try {
					Color TempCol;
					if (((System.getProperty("os.name").startsWith("Win")) || (System
							.getProperty("os.name").startsWith("Sol"))))

						TempCol = JColorChooser.showDialog(null,
								"Choose Model Color", new Color(Cols[2],
										Cols[1], Cols[0]));
					else

						TempCol = JColorChooser.showDialog(null,
								"Choose Model Color", new Color(Cols[0],
										Cols[1], Cols[2]));

					if (TempCol != null)
						eng3D_Engine.intModelCol = eng3D_Engine.getCol(
								TempCol.getBlue(), TempCol.getGreen(),
								TempCol.getRed());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
				}
			}
		};
		jMnuItm_ModelColor.addActionListener(actnLstnr_ModelColor);

		jMnuItm_TextColor = new JMenuItem("Text Color");
		jMnuItm_TextColor.setMnemonic(84);
		jMnuItm_TextColor.setAccelerator(KeyStroke.getKeyStroke(84, 2));

		actnLstnr_TextColor = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				int[] Cols = eng3D_Engine.getRGB(eng3D_Engine.intTextCol);
				try {
					Color TempCol;
					if (((System.getProperty("os.name").startsWith("Win")) || (System
							.getProperty("os.name").startsWith("Sol"))))

						TempCol = JColorChooser.showDialog(null,
								"Choose Text Color", new Color(Cols[2],
										Cols[1], Cols[0]));
					else

						TempCol = JColorChooser.showDialog(null,
								"Choose Text Color", new Color(Cols[0],
										Cols[1], Cols[2]));

					if (TempCol != null)
						eng3D_Engine.intTextCol = eng3D_Engine.getCol(
								TempCol.getBlue(), TempCol.getGreen(),
								TempCol.getRed());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
				}
			}
		};
		jMnuItm_TextColor.addActionListener(actnLstnr_TextColor);

		jMnuItm_BackGroundColor = new JMenuItem("Background Color");
		jMnuItm_BackGroundColor.setMnemonic(66);
		jMnuItm_BackGroundColor.setAccelerator(KeyStroke.getKeyStroke(66, 2));

		actnLstnr_BackGroundColor = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				int[] Cols = eng3D_Engine.getRGB(eng3D_Engine.intBackgroundCol);
				try {
					Color TempCol;
					if (((System.getProperty("os.name").startsWith("Win")) || (System
							.getProperty("os.name").startsWith("Sol"))))

						TempCol = JColorChooser.showDialog(null,
								"Choose Background Color", new Color(Cols[2],
										Cols[1], Cols[0]));
					else

						TempCol = JColorChooser.showDialog(null,
								"Choose Background Color", new Color(Cols[0],
										Cols[1], Cols[2]));

					if (TempCol != null)
						eng3D_Engine.intBackgroundCol = eng3D_Engine.getCol(
								TempCol.getBlue(), TempCol.getGreen(),
								TempCol.getRed());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
				}
			}
		};
		jMnuItm_BackGroundColor.addActionListener(actnLstnr_BackGroundColor);

		jMnu_Color.add(jMnuItm_ModelColor);
		jMnu_Color.add(jMnuItm_TextColor);
		jMnu_Color.add(jMnuItm_BackGroundColor);

		jMnuBr_Menu.add(jMnu_File);
		jMnuBr_Menu.add(jMnu_Edit);
		jMnuBr_Menu.add(jMnu_Color);

		jFrme_ControlFrame.setJMenuBar(jMnuBr_Menu);
	}

	private static void initModelTools() {

		jSldr_ModelRotX = new JSlider(0, -180, 180, 0);
		jSldr_ModelRotX.setMajorTickSpacing(5);
		jSldr_ModelRotX.setMinorTickSpacing(1);
		jSldr_ModelRotX.setBounds(40, 40, 250, 25);

		chngLstn_ModelRotX = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Object3D_Model.setXRot(jSldr_ModelRotX.getValue());
			}
		};
		jSldr_ModelRotX.addChangeListener(chngLstn_ModelRotX);
		jFrme_ControlFrame.getContentPane().add(jSldr_ModelRotX);

		jSldr_ModelRotY = new JSlider(0, -180, 180, 0);
		jSldr_ModelRotY.setMajorTickSpacing(5);
		jSldr_ModelRotY.setMinorTickSpacing(1);
		jSldr_ModelRotY.setBounds(40, 70, 250, 25);

		chngLstn_ModelRotY = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Object3D_Model.setYRot(jSldr_ModelRotY.getValue());
			}
		};
		jSldr_ModelRotY.addChangeListener(chngLstn_ModelRotY);
		jFrme_ControlFrame.getContentPane().add(jSldr_ModelRotY);

		jSldr_ModelRotZ = new JSlider(0, -180, 180, 0);
		jSldr_ModelRotZ.setMajorTickSpacing(5);
		jSldr_ModelRotZ.setMinorTickSpacing(1);
		jSldr_ModelRotZ.setBounds(40, 100, 250, 25);

		chngLstn_ModelRotZ = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Object3D_Model.setZRot(jSldr_ModelRotZ.getValue());
			}
		};
		jSldr_ModelRotZ.addChangeListener(chngLstn_ModelRotZ);
		jFrme_ControlFrame.getContentPane().add(jSldr_ModelRotZ);

		JLabel jLbl_ModelRotX = new JLabel("X°");
		jLbl_ModelRotX.setFont(new Font("Ariel", 0, 14));
		jLbl_ModelRotX.setBounds(15, 36, 50, 30);
		jFrme_ControlFrame.add(jLbl_ModelRotX);

		JLabel jLbl_ModelRotY = new JLabel("Y°");
		jLbl_ModelRotY.setFont(new Font("Ariel", 0, 14));
		jLbl_ModelRotY.setBounds(15, 66, 50, 30);
		jFrme_ControlFrame.add(jLbl_ModelRotY);

		JLabel jLbl_ModelRotZ = new JLabel("Z°");
		jLbl_ModelRotZ.setFont(new Font("Ariel", 0, 14));
		jLbl_ModelRotZ.setBounds(15, 96, 50, 30);
		jFrme_ControlFrame.add(jLbl_ModelRotZ);

		JLabel jLbl_ModelRotation = new JLabel("Model Rotation");
		jLbl_ModelRotation.setFont(new Font("Ariel", 0, 14));
		jLbl_ModelRotation.setBounds(15, 5, 200, 30);
		jFrme_ControlFrame.add(jLbl_ModelRotation);

		JLabel jLbl_ModelPosition = new JLabel("Model Position");
		jLbl_ModelPosition.setFont(new Font("Ariel", 0, 14));
		jLbl_ModelPosition.setBounds(15, 125, 200, 30);
		jFrme_ControlFrame.add(jLbl_ModelPosition);

		JLabel jLbl_ModelPosX = new JLabel("X");
		jLbl_ModelPosX.setFont(new Font("Ariel", 0, 14));
		jLbl_ModelPosX.setBounds(15, 150, 50, 30);
		jFrme_ControlFrame.add(jLbl_ModelPosX);

		JLabel jLbl_ModelPosY = new JLabel("Y");
		jLbl_ModelPosY.setFont(new Font("Ariel", 0, 14));
		jLbl_ModelPosY.setBounds(105, 150, 50, 30);
		jFrme_ControlFrame.add(jLbl_ModelPosY);

		JLabel jLbl_ModelPosZ = new JLabel("Z");
		jLbl_ModelPosZ.setFont(new Font("Ariel", 0, 14));
		jLbl_ModelPosZ.setBounds(195, 150, 50, 30);
		jFrme_ControlFrame.add(jLbl_ModelPosZ);

		jTxtFld_ModelPosX = new JTextField(2);
		jTxtFld_ModelPosX.setBounds(30, 160, 65, 20);

		actnLstnr_ModelPosX = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					float float_ModelPosX = Float.parseFloat(jTxtFld_ModelPosX
							.getText());
					Object3D_Model.setXPos(checkValue(float_ModelPosX));
					getValues();
				} catch (Exception E) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
					getValues();
				}
			}
		};
		jTxtFld_ModelPosX.addActionListener(actnLstnr_ModelPosX);
		jFrme_ControlFrame.add(jTxtFld_ModelPosX);

		jTxtFld_ModelPosY = new JTextField(2);
		jTxtFld_ModelPosY.setBounds(120, 160, 65, 20);

		actnLstnr_ModelPosY = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					float float_ModelPosY = Float.parseFloat(jTxtFld_ModelPosY
							.getText());
					Object3D_Model.setYPos(checkValue(float_ModelPosY));
					getValues();
				} catch (Exception E) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
					getValues();
				}
			}
		};
		jTxtFld_ModelPosY.addActionListener(actnLstnr_ModelPosY);
		jFrme_ControlFrame.add(jTxtFld_ModelPosY);

		jTxtFld_ModelPosZ = new JTextField(2);
		jTxtFld_ModelPosZ.setBounds(210, 160, 65, 20);

		actnLstnr_ModelPosZ = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					float float_ModelPosZ = Float.parseFloat(jTxtFld_ModelPosZ
							.getText());

					if ((float_ModelPosZ - (Object3D_Model.Size / 2)) < (eng3D_Engine.CamPos
							.Z() - 9)) {
						Object3D_Model.setZPos((eng3D_Engine.CamPos.Z() - 9)
								+ (Object3D_Model.Size / 2));
						getValues();
						return;
					}

					Object3D_Model.setZPos(checkValue(float_ModelPosZ));
					getValues();
				} catch (Exception E) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
					getValues();
				}
			}
		};
		jTxtFld_ModelPosZ.addActionListener(actnLstnr_ModelPosZ);
		jFrme_ControlFrame.add(jTxtFld_ModelPosZ);

		jTxtFld_CameraRotX = new JTextField(2);
		jTxtFld_CameraRotX.setBounds(30, 215, 65, 20);

		actnLstnr_CameraRotX = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					float float_CameraRotX = Float
							.parseFloat(jTxtFld_CameraRotX.getText());
					eng3D_Engine.setCamRotX(float_CameraRotX);
					getValues();
				} catch (Exception E) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
					getValues();
				}
			}
		};
		jTxtFld_CameraRotX.addActionListener(actnLstnr_CameraRotX);
		jFrme_ControlFrame.add(jTxtFld_CameraRotX);

		jTxtFld_CameraRotY = new JTextField(2);
		jTxtFld_CameraRotY.setBounds(120, 215, 65, 20);

		actnLstnr_CameraRotY = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					float float_CameraRotY = Float
							.parseFloat(jTxtFld_CameraRotY.getText());
					eng3D_Engine.setCamRotY(float_CameraRotY);
					getValues();
				} catch (Exception E) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
					getValues();
				}
			}
		};
		jTxtFld_CameraRotY.addActionListener(actnLstnr_CameraRotY);
		jFrme_ControlFrame.add(jTxtFld_CameraRotY);

		jTxtFld_CameraRotZ = new JTextField(2);
		jTxtFld_CameraRotZ.setBounds(210, 215, 65, 20);

		actnLstnr_CameraRotZ = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					float float_CameraRotZ = Float
							.parseFloat(jTxtFld_CameraRotZ.getText());
					eng3D_Engine.setCamRotZ(float_CameraRotZ);
					getValues();
				} catch (Exception E) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
					getValues();
				}
			}
		};
		jTxtFld_CameraRotZ.addActionListener(actnLstnr_CameraRotZ);
		jFrme_ControlFrame.add(jTxtFld_CameraRotZ);

		JLabel jLbl_CameraRotX = new JLabel("X°");
		jLbl_CameraRotX.setFont(new Font("Ariel", 0, 14));
		jLbl_CameraRotX.setBounds(15, 205, 50, 30);
		jFrme_ControlFrame.add(jLbl_CameraRotX);

		JLabel jLbl_CameraRotY = new JLabel("Y°");
		jLbl_CameraRotY.setFont(new Font("Ariel", 0, 14));
		jLbl_CameraRotY.setBounds(105, 205, 50, 30);
		jFrme_ControlFrame.add(jLbl_CameraRotY);

		JLabel jLbl_CameraRotZ = new JLabel("Z°");
		jLbl_CameraRotZ.setFont(new Font("Ariel", 0, 14));
		jLbl_CameraRotZ.setBounds(195, 205, 50, 30);
		jFrme_ControlFrame.add(jLbl_CameraRotZ);

		JLabel jLbl_CameraRotation = new JLabel("Camera Rotation");
		jLbl_CameraRotation.setFont(new Font("Ariel", 0, 14));
		jLbl_CameraRotation.setBounds(15, 180, 200, 30);
		jFrme_ControlFrame.add(jLbl_CameraRotation);

		JLabel jLbl_CameraPositon = new JLabel("Camera Position");
		jLbl_CameraPositon.setFont(new Font("Ariel", 0, 14));
		jLbl_CameraPositon.setBounds(15, 235, 200, 30);
		jFrme_ControlFrame.add(jLbl_CameraPositon);

		JLabel jLbl_CameraPosX = new JLabel("X");
		jLbl_CameraPosX.setFont(new Font("Ariel", 0, 14));
		jLbl_CameraPosX.setBounds(15, 260, 50, 30);
		jFrme_ControlFrame.add(jLbl_CameraPosX);

		JLabel jLbl_CameraPosY = new JLabel("Y");
		jLbl_CameraPosY.setFont(new Font("Ariel", 0, 14));
		jLbl_CameraPosY.setBounds(105, 260, 50, 30);
		jFrme_ControlFrame.add(jLbl_CameraPosY);

		JLabel jLbl_CameraPosZ = new JLabel("Z");
		jLbl_CameraPosZ.setFont(new Font("Ariel", 0, 14));
		jLbl_CameraPosZ.setBounds(195, 260, 50, 30);
		jFrme_ControlFrame.add(jLbl_CameraPosZ);

		jTxtFld_CameraPosX = new JTextField(2);
		jTxtFld_CameraPosX.setBounds(30, 270, 65, 20);

		actnLstnr_CameraPosX = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					float float_CameraPosX = Float
							.parseFloat(jTxtFld_CameraPosX.getText());
					eng3D_Engine.setCamPosX(checkValue(float_CameraPosX));
					getValues();
				} catch (Exception E) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
					getValues();
				}
			}
		};
		jTxtFld_CameraPosX.addActionListener(actnLstnr_CameraPosX);
		jFrme_ControlFrame.add(jTxtFld_CameraPosX);

		jTxtFld_CameraPosY = new JTextField(2);
		jTxtFld_CameraPosY.setBounds(120, 270, 65, 20);

		actnLstnr_CameraPosY = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					float float_CameraPosY = Float
							.parseFloat(jTxtFld_CameraPosY.getText());
					eng3D_Engine.setCamPosY(checkValue(float_CameraPosY));
					getValues();
				} catch (Exception E) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
					getValues();
				}
			}
		};
		jTxtFld_CameraPosY.addActionListener(actnLstnr_CameraPosY);
		jFrme_ControlFrame.add(jTxtFld_CameraPosY);

		jTxtFld_CameraPosZ = new JTextField(2);
		jTxtFld_CameraPosZ.setBounds(210, 270, 65, 20);

		actnLstnr_CameraPosZ = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					float float_CameraPosZ = Float
							.parseFloat(jTxtFld_CameraPosZ.getText());

					if ((Object3D_Model.ModelPos.Z() - (Object3D_Model.Size / 2)) < (float_CameraPosZ + 9)) {
						eng3D_Engine
								.setCamPosZ(checkValue((Object3D_Model.ModelPos
										.Z() + 9) - (Object3D_Model.Size / 2)));
						System.out.println("Whut");
						getValues();
						return;
					}
					eng3D_Engine.setCamPosZ(checkValue(float_CameraPosZ));
					getValues();
				} catch (Exception E) {
					JOptionPane.showMessageDialog(null,
							"Invalid Data Supplied!");
					getValues();
				}
			}
		};
		jTxtFld_CameraPosZ.addActionListener(actnLstnr_CameraPosZ);
		jFrme_ControlFrame.add(jTxtFld_CameraPosZ);
	}

	private static void initDrawOptions() {

		jChkbox_Animation = new JCheckBox("Animation");
		jChkbox_Animation.setBounds(15, 300, 150, 25);
		jChkbox_Animation.setSelected(false);

		actnLstnr_Animation = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolDoAnimation = !eng3D_Engine.boolDoAnimation;
			}
		};
		jChkbox_Animation.addActionListener(actnLstnr_Animation);
		jFrme_ControlFrame.getContentPane().add(jChkbox_Animation);

		jChkbox_BackfaceCulling = new JCheckBox("Backface Culling");
		jChkbox_BackfaceCulling.setBounds(15, 320, 150, 25);
		jChkbox_BackfaceCulling.setSelected(false);

		actnLstnr_BackfaceCulling = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolBackFaces = (!eng3D_Engine.boolBackFaces);
			}
		};
		jChkbox_BackfaceCulling.addActionListener(actnLstnr_BackfaceCulling);
		jFrme_ControlFrame.getContentPane().add(jChkbox_BackfaceCulling);

		jChkbox_ZCulling = new JCheckBox("Depth Culling");
		jChkbox_ZCulling.setBounds(15, 340, 150, 25);
		jChkbox_ZCulling.setSelected(false);

		actnLstnr_ZCulling = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolZCulling = (!eng3D_Engine.boolZCulling);
			}
		};
		jChkbox_ZCulling.addActionListener(actnLstnr_ZCulling);
		jFrme_ControlFrame.getContentPane().add(jChkbox_ZCulling);

		jChkbox_TrianglePoints = new JCheckBox("Triangle Points");
		jChkbox_TrianglePoints.setBounds(15, 360, 150, 25);
		jChkbox_TrianglePoints.setSelected(true);

		actnLstnr_TrianglePoints = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolTrianglePoints = (!eng3D_Engine.boolTrianglePoints);
				eng3D_Engine.boolTriangleMesh = false;
				eng3D_Engine.boolTriangleFaces = false;
				jChkbox_TriangleMesh.setSelected(false);
				jChkbox_TriangleFace.setSelected(false);
			}
		};
		jChkbox_TrianglePoints.addActionListener(actnLstnr_TrianglePoints);
		jFrme_ControlFrame.getContentPane().add(jChkbox_TrianglePoints);

		jChkbox_TriangleMesh = new JCheckBox("Triangle Mesh");
		jChkbox_TriangleMesh.setBounds(15, 380, 150, 25);
		jChkbox_TriangleMesh.setSelected(false);

		actnLstnr_TriangleMesh = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolTriangleMesh = (!eng3D_Engine.boolTriangleMesh);
				eng3D_Engine.boolTriangleFaces = false;
				eng3D_Engine.boolTrianglePoints = false;
				jChkbox_TriangleFace.setSelected(false);
				jChkbox_TrianglePoints.setSelected(false);
			}
		};
		jChkbox_TriangleMesh.addActionListener(actnLstnr_TriangleMesh);
		jFrme_ControlFrame.getContentPane().add(jChkbox_TriangleMesh);

		jChkbox_TriangleFace = new JCheckBox("Triangle Faces");
		jChkbox_TriangleFace.setBounds(15, 400, 150, 25);
		jChkbox_TriangleFace.setSelected(false);

		actnLstnr_TriangleFace = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolTriangleFaces = (!eng3D_Engine.boolTriangleFaces);
				eng3D_Engine.boolTriangleMesh = false;
				eng3D_Engine.boolTrianglePoints = false;
				jChkbox_TrianglePoints.setSelected(false);
				jChkbox_TriangleMesh.setSelected(false);
			}
		};
		jChkbox_TriangleFace.addActionListener(actnLstnr_TriangleFace);
		jFrme_ControlFrame.getContentPane().add(jChkbox_TriangleFace);

		jChkbox_TriangleShading = new JCheckBox("Triangle Shading");
		jChkbox_TriangleShading.setBounds(15, 420, 150, 25);
		jChkbox_TriangleShading.setSelected(false);

		actnLstnr_TriangleShading = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolShading = (!eng3D_Engine.boolShading);
				eng3D_Engine.boolZCulling = true;
				jChkbox_ZCulling.setSelected(true);
			}
		};
		jChkbox_TriangleShading.addActionListener(actnLstnr_TriangleShading);
		jFrme_ControlFrame.getContentPane().add(jChkbox_TriangleShading);

		jChkbox_ObjectNames = new JCheckBox("Object Name");
		jChkbox_ObjectNames.setBounds(165, 300, 180, 25);
		jChkbox_ObjectNames.setSelected(false);

		actnLstnr_ObjectNames = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolNames = (!eng3D_Engine.boolNames);
			}
		};
		jChkbox_ObjectNames.addActionListener(actnLstnr_ObjectNames);
		jFrme_ControlFrame.getContentPane().add(jChkbox_ObjectNames);

		jChkbox_ViewModelMatrix = new JCheckBox("Model Matrix");
		jChkbox_ViewModelMatrix.setBounds(165, 320, 180, 25);
		jChkbox_ViewModelMatrix.setSelected(false);

		actnLstnr_ViewModelMatrix = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolShowModelMatrix = !eng3D_Engine.boolShowModelMatrix;
			}
		};
		jChkbox_ViewModelMatrix.addActionListener(actnLstnr_ViewModelMatrix);
		jFrme_ControlFrame.getContentPane().add(jChkbox_ViewModelMatrix);

		jChkbox_ViewCameraMatrix = new JCheckBox("Camera Matrix");
		jChkbox_ViewCameraMatrix.setBounds(165, 340, 180, 25);
		jChkbox_ViewCameraMatrix.setSelected(false);

		actnLstnr_ViewCameraMatrix = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolShowViewMatrix = !eng3D_Engine.boolShowViewMatrix;
			}
		};
		jChkbox_ViewCameraMatrix.addActionListener(actnLstnr_ViewCameraMatrix);
		jFrme_ControlFrame.getContentPane().add(jChkbox_ViewCameraMatrix);

		jChkbox_ViewModelData = new JCheckBox("Model Data");
		jChkbox_ViewModelData.setBounds(165, 360, 180, 25);
		jChkbox_ViewModelData.setSelected(false);

		actnLstnr_ViewModelData = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eng3D_Engine.boolShowModelData = !eng3D_Engine.boolShowModelData;
			}
		};
		jChkbox_ViewModelData.addActionListener(actnLstnr_ViewModelData);
		jFrme_ControlFrame.getContentPane().add(jChkbox_ViewModelData);

	}

	private static boolean between(float Value, float Low, float High) {
		return ((Low <= Value) && (Value <= High));
	}

	private static float checkValue(float Value) {
		if (!between(Value, -360, 360)) {
			if (Value < 0)
				return -360;
			else
				return 360;
		} else
			return Value;
	}

	private static void getValues() {

		jTxtFld_ModelPosX.setText(Float.toString(Object3D_Model.ModelPos.X()));
		jTxtFld_ModelPosY.setText(Float.toString(Object3D_Model.ModelPos.Y()));
		jTxtFld_ModelPosZ.setText(Float.toString(Object3D_Model.ModelPos.Z()));

		if (Math.abs(eng3D_Engine.CamRot.X()) == 0.0F)
			jTxtFld_CameraRotX.setText(Float.toString(eng3D_Engine.CamRot.X()));
		else {
			jTxtFld_CameraRotX
					.setText(Float.toString(-eng3D_Engine.CamRot.X()));
		}
		jTxtFld_CameraRotY.setText(Float.toString(eng3D_Engine.CamRot.Y()));
		jTxtFld_CameraRotZ.setText(Float.toString(eng3D_Engine.CamRot.Z()));

		if (Math.abs(eng3D_Engine.CamPos.X()) == 0.0F)
			jTxtFld_CameraPosX.setText(Float.toString(eng3D_Engine.CamPos.X()));
		else {
			jTxtFld_CameraPosX
					.setText(Float.toString(-eng3D_Engine.CamPos.X()));
		}
		jTxtFld_CameraPosY.setText(Float.toString(eng3D_Engine.CamPos.Y()));
		jTxtFld_CameraPosZ.setText(Float.toString(eng3D_Engine.CamPos.Z()));
	}

	private static void logicLoop() {


		eng3D_Engine.drawObject(Object3D_Model);
		eng3D_Engine.render();
		eng3D_Engine.clearScreen();
		F += 1L;
		try {
			FPS = F / ((System.currentTimeMillis() - TimeRunning) / 1000L);
			Thread.sleep(1L);
		} catch (Exception e) {
		}
		eng3D_Engine.drawString(new PointD(20.0F, 20.0F), "Lucida Console",
				Long.toString(FPS), 14, 0, eng3D_Engine.intTextCol);

	}
}
