package javaclasses;

public class PointD {

	private float floatX;
	private float floatY;
	private float floatZ;

	public PointD() {
		this.floatX = 0.0F;
		this.floatY = 0.0F;
		this.floatZ = 0.0F;
	}

	public PointD(float floatX_, float floatY_, float floatZ_) {
		this.floatX = floatX_;
		this.floatY = floatY_;
		this.floatZ = floatZ_;
	}

	public PointD(float floatX_, float floatY_) {
		this.floatX = floatX_;
		this.floatY = floatY_;
	}

	public PointD(double doubleX_, double doubleY_, double doubleZ_) {
		this.floatX = (float) doubleX_;
		this.floatY = (float) doubleY_;
		this.floatZ = (float) doubleZ_;
	}

	public PointD(double doubleX_, double doubleY_) {
		this.floatX = (float) doubleX_;
		this.floatY = (float) doubleY_;
	}

	public float X() {
		return this.floatX;
	}

	public float Y() {
		return this.floatY;
	}

	public float Z() {
		return this.floatZ;
	}

	public void setX(float _x) {
		this.floatX = _x;
	}

	public void setY(float _y) {
		this.floatY = _y;
	}

	public void setZ(float _z) {
		this.floatZ = _z;
	}

	public void set(float _x, float _y, float _z) {
		this.floatX = _x;
		this.floatY = _y;
		this.floatZ = _z;
	}

	public void set(float _x, float _y) {
		this.floatX = _x;
		this.floatY = _y;
	}

	public String toString() {
		return "[" + String.valueOf(this.floatX) + ", "
				+ String.valueOf(this.floatY) + ", "
				+ String.valueOf(this.floatZ) + "]";
	}
}
