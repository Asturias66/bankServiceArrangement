package cn.edu.zuel.flowSheet;

/**
 * @Auther: zdp
 * @Date: 2022/1/21 12:04
 * @Description:
 */
public class JsonNode {
    private String current;
    private String next;
    private String name;
    private String why;
    private String nextName;
    private String parameter;
    private int isJumped;

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWhy() {
        return why;
    }

    public void setWhy(String why) {
        this.why = why;
    }

    public String getNextName() {
        return nextName;
    }

    public void setNextName(String nextName) {
        this.nextName = nextName;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public int getIsJumped() {
        return isJumped;
    }

    public void setIsJumped(int isJumped) {
        this.isJumped = isJumped;
    }
}
