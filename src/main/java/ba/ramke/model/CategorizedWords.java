package ba.ramke.model;

public class CategorizedWords {

	public String _id;
	public int y;


	public CategorizedWords(String _id, int y) {
		super();
		this._id = _id;
		this.y = y;
	}

	public String get_Id() {
		return _id;
	}

	public void set_Id(String _id) {
		this._id = _id;
	}
	
	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}