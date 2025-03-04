package nz.ac.wgtn.ecs.jdala;

import com.google.common.collect.MapMaker;
import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShadedClassesTests extends StaticAgentTests{

    @Test
    public void testSynchronizedMap1() {
        Map<Object, Thread> synchronizedMap = Collections.synchronizedMap(new IdentityHashMap<>());

        synchronizedMap.put(new Object(), Thread.currentThread());
    }

    @Test
    public void testGuava1() {
        Map<Object, Thread> guavaMap = new MapMaker().concurrencyLevel(4).weakKeys().makeMap();

        guavaMap.put(new Object(), Thread.currentThread());
    }

    @Test
    public void testJSONOrg1() {
        String jsonText = "{classes:[class]}";
        JSONObject rootNode = new JSONObject(jsonText);
        JSONArray classesArray = rootNode.optJSONArray("classes");

        System.out.println(classesArray.getClass().getPackageName());
    }

    /**
     * JSON.org is one of the shaded classes but any calls to it from outside {@link JDala}
     * should still adhere to the Dala Capability like {@link Immutable}
     */
    @Test
    public void testJSONOrg2() {
        String jsonText = "{classes:[class]}";
        @Immutable JSONObject rootNode = new JSONObject(jsonText);
        assertThrows(DalaCapabilityViolationException.class, () -> rootNode.put("new", "Illegal Object"));
    }
}
