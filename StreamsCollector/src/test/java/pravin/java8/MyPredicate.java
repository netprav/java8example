package pravin.java8;

import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by user1 on 17/01/2016.
 */
public class MyPredicate implements Predicate<MyField> {

    @Override
    public boolean test(MyField t) {
        return t.domainVariants.size()>0;
    }
}
