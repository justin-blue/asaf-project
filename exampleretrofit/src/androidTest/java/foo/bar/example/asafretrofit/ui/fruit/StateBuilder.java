package foo.bar.example.asafretrofit.ui.fruit;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import foo.bar.example.asafretrofit.CustomApp;
import foo.bar.example.asafretrofit.api.fruits.FruitPojo;
import foo.bar.example.asafretrofit.feature.fruit.FruitFetcher;

import static org.mockito.Mockito.when;

/**
 *
 */
public class StateBuilder {

    private FruitFetcher mockFruitFetcher;

    StateBuilder(FruitFetcher mockFruitFetcher) {
        this.mockFruitFetcher = mockFruitFetcher;
    }

    StateBuilder isBusy(boolean busy) {
        when(mockFruitFetcher.isBusy()).thenReturn(busy);
        return this;
    }

    StateBuilder hasFruit(FruitPojo fruitPojo) {
        when(mockFruitFetcher.getCurrentFruit()).thenReturn(fruitPojo);
        return this;
    }

    ActivityTestRule<FruitActivity>  createRule(){

        return new ActivityTestRule<FruitActivity>(FruitActivity.class) {
            @Override
            protected void beforeActivityLaunched() {

                //get hold of the application
                CustomApp customApp = (CustomApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
                customApp.injectSynchronousObjectGraph();

                //inject our mocks so our UI layer will pick them up
                customApp.injectMockObject(FruitFetcher.class, mockFruitFetcher);
            }

        };
    }

}
