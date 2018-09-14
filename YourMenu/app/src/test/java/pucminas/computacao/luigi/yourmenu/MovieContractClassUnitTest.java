package pucminas.computacao.luigi.yourmenu;

import org.junit.Assert;
import org.junit.Test;

public class MovieContractClassUnitTest {

    @Test
    public void innerClassValidator_InnerClassExists_ReturnsTrue() {
        try {
            Class.forName("pucminas.computacao.luigi.yourmenu.database.movie.MovieContract$MovieEntry");
        } catch (ClassNotFoundException e) {
            Assert.fail("Should have an inner class called MovieEntry");
        }
    }
}
