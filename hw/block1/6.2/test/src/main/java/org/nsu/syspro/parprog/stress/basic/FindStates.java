package org.nsu.syspro.parprog.stress.basic;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.III_Result;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;


@JCStressTest
@State
@Outcome(id = "0, 2, 1", expect = ACCEPTABLE, desc = "B.1->B.2->B.3->B.4->A.1->A.2->A.3->C.1->C.2->C.3->C.4")
@Outcome(id = "1, 0, 1", expect = ACCEPTABLE, desc = "A.1->A.2->A.3->B.1->B.2->B.3->B.4->C.1->C.2")
@Outcome(id = "1, 1, 1", expect = ACCEPTABLE, desc = "B.1->B.2->A.1->A.2->A.3->B.3->B.4->C.1->C.2")
@Outcome(id = "1, 2, 1", expect = ACCEPTABLE, desc = "C.1->C.2->B.1->B.2->B.3->B.4->A.1->A.2->A.3")
@Outcome(id = "-1, -1, 1", expect = ACCEPTABLE_INTERESTING)
@Outcome(id = "-1, 0, 1", expect = ACCEPTABLE_INTERESTING)
@Outcome(id = "-1, 1, 1", expect = ACCEPTABLE_INTERESTING)
@Outcome(id = "-1, 2, 1", expect = ACCEPTABLE_INTERESTING)
@Outcome(id = "0, -1, 1", expect = ACCEPTABLE_INTERESTING)
@Outcome(id = "0, 0, 1", expect = ACCEPTABLE_INTERESTING)
@Outcome(id = "0, 1, 1", expect = ACCEPTABLE_INTERESTING)
@Outcome(id = "1, -1, 1", expect = ACCEPTABLE_INTERESTING)
public class FindStates {
    int x, y, z;

    @Actor 
    public void a() {
        int a_x = x;
        int a_z = z;
        y = a_x + a_z;
    }
    
    @Actor 
    public void b() {
        int b_x = x;
        x = b_x + 1;
        int b_z = z;
        z = b_z + 1;
    }
    
    @Actor 
    public void c() {
        int c_y = y;
        if (c_y == 2) {
            int c_x = x;
            x = c_x - 1;
        }
    }

    @Arbiter
    public void main(III_Result r) {
        r.r1 = x;
        r.r2 = y;
        r.r3 = z;
    }
}
