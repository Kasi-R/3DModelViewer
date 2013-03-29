package javaclasses;

public class Triangle3D {

	public PointD a = new PointD(0.0F, 0.0F, 0.0F);
	public PointD b = new PointD(0.0F, 0.0F, 0.0F);
	public PointD c = new PointD(0.0F, 0.0F, 0.0F);

	public Triangle3D() {
	}

	public Triangle3D(PointD _a, PointD _b, PointD _c) {
		this.a = _a;
		this.b = _b;
		this.c = _c;
	}

}
