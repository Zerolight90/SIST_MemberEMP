package vo;

public class DocsVO {
    private String docs_num, empno, deptno, title, visibility, logs_num, content, date, co_letter;
    private DeptVO dcvo;

    public DeptVO getDcvo() {
        return dcvo;
    }

    public DeptVO getDvo() {
        return dcvo;
    }

    public void setDcvo(DeptVO dvo) {
        this.dcvo = dvo;
    }

    public String getDocs_num() {
        return docs_num;
    }

    public void setDocs_num(String docs_num) {
        this.docs_num = docs_num;
    }

    public String getEmpno() {
        return empno;
    }

    public void setEmpno(String empno) {
        this.empno = empno;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getLogs_num() {
        return logs_num;
    }

    public void setLogs_num(String logs_num) {
        this.logs_num = logs_num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCo_letter() {
        return co_letter;
    }

    public void setCo_letter(String co_letter) {
        this.co_letter = co_letter;
    }
}
