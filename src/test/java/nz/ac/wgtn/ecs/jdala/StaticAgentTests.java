package nz.ac.wgtn.ecs.jdala;

import org.junit.jupiter.api.BeforeEach;

public abstract class StaticAgentTests {

    @BeforeEach
    void reset(){
        JDala.reset();
    }
}
