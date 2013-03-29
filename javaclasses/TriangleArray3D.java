package javaclasses;

public class TriangleArray3D {

	public Triangle3D E[];

	public TriangleArray3D() {
		E = new Triangle3D[1];
		E[0] = new Triangle3D();
	}

	public TriangleArray3D(int I) {
		E = new Triangle3D[I];

		for (int II = 0; II < I; II++) {
			E[II] = new Triangle3D();
		}
	}
}
