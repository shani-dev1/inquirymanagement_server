package Data;

import java.io.Serializable;

public class Representative implements Serializable {
    private int code;
    private int id;
    private String name;
    private static int representativeNextCodeVal = 0;

    public static int getRepresentativeNextCodeVal() {
        return representativeNextCodeVal;
    }

    public static void setRepresentativeNextCodeVal(int value) {
        representativeNextCodeVal = value;
    }

    public Representative(Integer id, String name) {
        this.code = representativeNextCodeVal++;
        this.id = id;
        this.name = name;
    }

    public Representative() {}

    public int getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Representative{" +
                "code=" + code +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}