package javaclasses;

import java.util.ArrayList;

public class PointDArray {
	public ArrayList<PointD> E;

	public PointDArray() {
		this.E = new ArrayList<PointD>();
	}

	public PointDArray(int I) {
		this.E = new ArrayList<PointD>(I);
	}

}
