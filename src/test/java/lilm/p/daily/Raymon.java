package lilm.p.daily;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Raymon {
    private static final String INFO = "info", CONTENT = "content";
    
    public static void main(String[] args) {
        String[] aboutMeArr = {INFO, CONTENT};
        for (String item : aboutMeArr) {
            System.out.println(detail().apply(item));
        }
    }
    
    private static Function<String, Map> detail() {
        return (x) -> {
            Map<String, Object> detail = new HashMap<>();
            switch (x) {
                case INFO:
                    detail.put("RealName", getRealName());
                    detail.put("Age", 0x18);
                    detail.put("GraduateTime", "2015");
                    detail.put("GraduateFrom", "hdu");
                    detail.put("title", "Java程序员(应该已经看出来了吧= =)");
                    break;
                case CONTENT:
                    detail.put("Framework", Arrays.asList("Spring-*", "Mybatis"));
                    detail.put("MiddleSoftware", Arrays.asList("ActiveMQ", "ZooKeeper", "Nginx"));
                    detail.put("DB", Arrays.asList("Mysql", "Pgsql", "Redis", "Mongodb", "ES"));
                    detail.put("Tools", Arrays.asList("Shell", "Git", "Maven", "IntelliJ IDEA"));
                    break;
                default:
                    break;
            }
            return detail;
        };
    }
    
    private static String getRealName() {
        return Arrays.stream("\\u9ece\\u4e50\\u654f".split("\\\\u"))
                .map(c -> c.isEmpty() ? "" : String.valueOf((char) Integer.valueOf(c, 16).intValue()))
                .collect(Collectors.joining(""));
    }
    
}
