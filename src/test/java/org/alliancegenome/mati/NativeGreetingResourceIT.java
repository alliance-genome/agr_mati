package org.alliancegenome.mati;

import io.quarkus.test.junit.NativeImageTest;
import org.alliancegenome.mati.GreetingResourceTest;

@NativeImageTest
public class NativeGreetingResourceIT extends GreetingResourceTest {

    // Execute the same tests but in native mode.
}