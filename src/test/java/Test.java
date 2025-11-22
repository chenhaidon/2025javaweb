import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    Integer [] myArray = { 1, 2, 3 };
    List myList = Arrays.stream(myArray).collect(Collectors.toList());

    Integer[] array = {1, 2, 3};
    List<Integer> list = List.of(array);

}
