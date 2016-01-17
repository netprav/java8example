package pravin.java8;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */

    public AppTest( String testName ) {
        super( testName );
        System.out.print("hello world");

        Map<Integer, Map<Integer, List<MyField>>> entityTypeMap
                    = new HashMap<Integer, Map<Integer, List<MyField>>>();

        HashMap<Integer, List<MyField>> entityMap = new HashMap<Integer, List<MyField>>();
        List<MyField> fieldList = Arrays.asList(
                createNewFieldWithVariant(0,1), createNewFieldWithoutVariant(),
                createNewFieldWithVariant(2,0), createNewFieldWithVariant(1,1),
                createNewFieldWithoutVariant(0,0));

        entityMap.put(1001, fieldList);
        entityTypeMap.put(1, entityMap);

        Map<Integer, Map<Integer, List<MyField>>> resultMap =
                entityTypeMap.entrySet().stream().collect(new MyCustomCollector());
        System.out.print(resultMap);
    }

    class MyCustomCollector implements Collector
            <Map.Entry<Integer, Map<Integer, List<MyField>>>,
                    Map<Integer, Map<Integer, List<MyField>>>,
                    Map<Integer, Map<Integer, List<MyField>>>> {

        public Supplier<Map<Integer, Map<Integer, List<MyField>>>> supplier() {
            return () -> {
                HashMap<Integer, Map<Integer, List<MyField>>> hm = new HashMap();
                //  Map<entityTypeID, Map<EntityId, List<Fields>>>, Each Field has domainVariantList
                return hm;
            };
        }

        // BiConsumer (Accumulator from Supplier function, Next Entry in Stream)
        public BiConsumer<Map<Integer, Map<Integer, List<MyField>>>,
                            Map.Entry<Integer, Map<Integer, List<MyField>>>> accumulator() {
            return (accu, nextElem) -> {
                Integer nextEntityTypeKey = nextElem.getKey();
                Map<Integer, List<MyField>> nextEntityTypeValue = nextElem.getValue();
                final Map<Integer, List<MyField>> accuEnityTypeEntry
                        = (accu.get(nextEntityTypeKey) ==null ) ?
                            new HashMap<Integer, List<MyField>>()
                                : accu.get(nextEntityTypeKey);

                accu.put(nextEntityTypeKey, accuEnityTypeEntry);

                List<MyField> variantFields = new ArrayList<MyField>();
                nextEntityTypeValue.entrySet().forEach((entityEntry)->{
                    Integer entityId = entityEntry.getKey();
                    variantFields.addAll(entityEntry.getValue().stream().filter((each)
                            -> each.domainVariants.size()>0).collect(Collectors.toList()));
                    accuEnityTypeEntry.put(entityId, variantFields);
                });
            };
        }

        public BinaryOperator<Map<Integer, Map<Integer, List<MyField>>>> combiner() {
            return (acc1, acc2) -> {
                throw new IllegalStateException("Does not support paralle Stream");
            };
        }

        public Function<Map<Integer, Map<Integer, List<MyField>>>,
                            Map<Integer, Map<Integer, List<MyField>>>> finisher() {
            return (acc) -> acc;
        }

        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }


    static int fieldId = 0;
    MyField createNewFieldWithVariant(Integer... n){
        MyField myField = new MyField();
        myField.fieldId = ++fieldId;
        myField.domainVariants = Arrays.asList(
                new MyDomainVariant(n[0]), new MyDomainVariant(n[1]));
        return myField;
    }

    MyField createNewFieldWithoutVariant(Integer... n){
        MyField myField = new MyField();
        myField.fieldId = ++fieldId;
        myField.domainVariants = new ArrayList<>();
        return myField;
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
