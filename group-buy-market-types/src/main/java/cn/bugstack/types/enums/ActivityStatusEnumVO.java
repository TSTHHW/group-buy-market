package cn.bugstack.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ActivityStatusEnumVO {
    CREATE("0", "创建"),
    EFFECTIVE("1", "生效"),
    EXPIRED("2", "过期"),
    ABANDONED("3", "废弃"),
    ;

    public static ActivityStatusEnumVO valueOf(Integer code){
        switch (code){
            case 0: return CREATE;
            case 1: return EFFECTIVE;
            case 2: return EXPIRED;
            case 3: return ABANDONED;
        }
        throw new RuntimeException("err code not exist!");
    }

    private String code;
    private String info;
}
