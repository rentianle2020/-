import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Test1 {

    public static void main(String[] args) {
        List<String> list = List.of("1", "1", "2");
        Map<String, String> map =
                list.stream().collect(Collectors.toMap(str -> str, str -> str,(k,v) -> k + " " + v));

        map.entrySet().forEach(entry -> {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        });
    }
}
