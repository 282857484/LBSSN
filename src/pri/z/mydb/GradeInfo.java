package pri.z.mydb;

public class GradeInfo {

	private int gradeId;
	private String gradePhone;
	private String gradeActId;
	private String gradeNum;
	private String gradeTime;
	
	
	
	public GradeInfo(int gradeId, String gradePhone, String gradeActId,
			String gradeNum, String gradeTime) {
		super();
		this.gradeId = gradeId;
		this.gradePhone = gradePhone;
		this.gradeActId = gradeActId;
		this.gradeNum = gradeNum;
		this.gradeTime = gradeTime;
	}
	public int getGradeId() {
		return gradeId;
	}
	public void setGradeId(int gradeId) {
		this.gradeId = gradeId;
	}
	public String getGradePhone() {
		return gradePhone;
	}
	public void setGradePhone(String gradePhone) {
		this.gradePhone = gradePhone;
	}
	public String getGradeActId() {
		return gradeActId;
	}
	public void setGradeActId(String gradeActId) {
		this.gradeActId = gradeActId;
	}
	public String getGradeNum() {
		return gradeNum;
	}
	public void setGradeNum(String gradeNum) {
		this.gradeNum = gradeNum;
	}
	public String getGradeTime() {
		return gradeTime;
	}
	public void setGradeTime(String gradeTime) {
		this.gradeTime = gradeTime;
	}
	@Override
	public String toString() {
		return "GradeInfo [gradeId=" + gradeId + ", gradePhone=" + gradePhone
				+ ", gradeActId=" + gradeActId + ", gradeNum=" + gradeNum
				+ ", gradeTime=" + gradeTime + "]";
	}
	
	
}
