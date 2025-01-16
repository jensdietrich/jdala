package nz.ac.wgtn.ecs.jdala;

import org.junit.jupiter.api.AfterEach;

public abstract class StaticAgentTests {

    @AfterEach
    void reset(){
        JDala.reset();
    }
}
