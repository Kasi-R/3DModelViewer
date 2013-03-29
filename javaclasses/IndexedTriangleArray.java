package javaclasses;

public class IndexedTriangleArray {

	public IndexedTriangle[] E;

	public IndexedTriangleArray() {
		this.E = new IndexedTriangle[1];
		this.E[0] = new IndexedTriangle();
	}

	public IndexedTriangleArray(int I) {

		this.E = new IndexedTriangle[I];

		for (int II = 0; II < I; II++)
			this.E[II] = new IndexedTriangle();
	}
}
