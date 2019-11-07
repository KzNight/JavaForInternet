public class MissonTwoPart3 {

	private String flid;   // 航班标识
	private String ffid;  // 航班号
	private String fromwhere;  // 来自于
	private String felt;  // 预计到达时间
	private String frlt; // 实际到达时间
	private String mark;  // 备注

	public MissonTwoPart3() {
		this.flid = null;
		this.ffid = null;
		this.fromwhere = null;
		this.felt = "暂无信息";
		this.frlt = null;
		this.mark = null;
	}

	
	public String getFlid() {
		return flid;
	}

	public void setFlid(String flid) {
		this.flid = flid;
	}

	public String getFfid() {
		return ffid;
	}

	public void setFfid(String ffid) {
		this.ffid = ffid;
	}

	public String getFromwhere() {
		return fromwhere;
	}

	public void setFromwhere(String fromwhere) {
		this.fromwhere = fromwhere;
	}

	public String getFelt() {
		return felt;
	}

	public void setFelt(String felt) {
		this.felt = felt;
	}
	
	public String getFrlt() {
		return frlt;
	}
	
	public void setFrlt(String frlt) {
		this.frlt = frlt;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
}
