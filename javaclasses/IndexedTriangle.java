package javaclasses;

public class IndexedTriangle {

	public PointDArray Vertices;

	public IndexedTriangle() {
		this.Vertices = new PointDArray();
	}

	public void setVertex(PointD P1, PointD P2, PointD P3) {
		this.Vertices.E.set(0, P1);
		this.Vertices.E.set(1, P2);
		this.Vertices.E.set(2, P3);
	}

}
