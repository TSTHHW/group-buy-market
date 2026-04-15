package cn.bugstack.infrastructure.dcc;

import cn.bugstack.types.annotation.DCCValue;
import org.springframework.stereotype.Service;

@Service
public class DCCService {

    @DCCValue("downgradeSwitch:0")
    private String downgradeSwitch;

    @DCCValue("cutRange:100")
    private String cutRange;

    public boolean isDowngradeSwitch(){
        return downgradeSwitch.equals("1");
    }

    public boolean isCutRange(String userId){
        int hashCode = Math.abs(userId.hashCode()) % 100;
        if(hashCode < Integer.parseInt(cutRange)){
            return true;
        }
        return false;
    }
}
