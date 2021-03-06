package foo.bar.example.asafretrofit.feature.fruit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.early.asaf.core.WorkMode;
import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallBack;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.logging.SystemLogger;
import co.early.asaf.core.observer.Observer;
import co.early.asaf.retrofit.CallProcessor;
import foo.bar.example.asafretrofit.api.fruits.FruitPojo;
import foo.bar.example.asafretrofit.api.fruits.FruitService;
import foo.bar.example.asafretrofit.message.UserMessage;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Tests for this model cover a few areas:
 * <p>
 * 1) Construction: we check that the model is constructed in the correct state
 * 2) Receiving data: we check that the model behaves appropriately when receiving various success and fail responses from the CallProcessor
 * 3) Observers and State: we check that the model updates its observers correctly and presents it's current state accurately
 *
 */
public class FruitFetcherUnitTest {

    public static final String TAG = FruitFetcherUnitTest.class.getSimpleName();

    private static Logger logger = new SystemLogger();
    private FruitPojo fruitPojo = new FruitPojo("strawberry", false, 71);

    private SuccessCallBack mockSuccessCallBack;
    private FailureCallbackWithPayload mockFailureCallbackWithPayload;
    private CallProcessor<UserMessage> mockCallProcessor;
    private FruitService mockFruitService;
    private Observer mockObserver;


    @Before
    public void setUp() throws Exception {
        mockSuccessCallBack = mock(SuccessCallBack.class);
        mockFailureCallbackWithPayload = mock(FailureCallbackWithPayload.class);
        mockCallProcessor = mock(CallProcessor.class);
        mockFruitService = mock(FruitService.class);
        mockObserver = mock(Observer.class);
    }


    @Test
    public void initialConditions() throws Exception {

        //arrange
        FruitFetcher fruitFetcher = new FruitFetcher(
                mockFruitService,
                mockCallProcessor,
                logger,
                WorkMode.SYNCHRONOUS);

        //act

        //assert
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
        Assert.assertEquals(false, fruitFetcher.getCurrentFruit().isCitrus);
    }


    @Test
    public void fetchFruit_MockSuccess() throws Exception {

        //arrange
        new StateBuilder(mockCallProcessor).getFruitSuccess(fruitPojo);

        FruitFetcher fruitFetcher = new FruitFetcher(
                mockFruitService,
                mockCallProcessor,
                logger,
                WorkMode.SYNCHRONOUS);


        //act
        fruitFetcher.fetchFruits(mockSuccessCallBack, mockFailureCallbackWithPayload);


        //assert
        verify(mockSuccessCallBack, times(1)).success();
        verify(mockFailureCallbackWithPayload, never()).fail(any());
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(fruitPojo.name, fruitFetcher.getCurrentFruit().name);
        Assert.assertEquals(fruitPojo.isCitrus, fruitFetcher.getCurrentFruit().isCitrus);
        Assert.assertEquals(fruitPojo.tastyPercentScore, fruitFetcher.getCurrentFruit().tastyPercentScore);
    }


    @Test
    public void fetchFruit_MockFailure() throws Exception {

        //arrange
        new StateBuilder(mockCallProcessor).getFruitFail(UserMessage.ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT);

        FruitFetcher fruitFetcher = new FruitFetcher(
                mockFruitService,
                mockCallProcessor,
                logger,
                WorkMode.SYNCHRONOUS);


        //act
        fruitFetcher.fetchFruits(mockSuccessCallBack, mockFailureCallbackWithPayload);


        //assert
        verify(mockSuccessCallBack, never()).success();
        verify(mockFailureCallbackWithPayload, times(1)).fail(eq(UserMessage.ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT));
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(false, fruitFetcher.getCurrentFruit().isCitrus);
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
    }


    /**
     *
     * NB all we are checking here is that observers are called AT LEAST once
     *
     * We don't really want tie our tests (OR any observers in production code)
     * to an expected number of times this method might be called. (This would be
     * testing an implementation detail and make the tests unnecessarily brittle)
     *
     * The contract says nothing about how many times the observers will get called,
     * only that they will be called if something changes ("something" is not defined
     * and can change between implementations).
     *
     * See the databinding docs for more information about this
     *
     * @throws Exception
     */
    @Test
    public void observersNotifiedAtLeastOnce() throws Exception {

        //arrange
        new StateBuilder(mockCallProcessor).getFruitSuccess(fruitPojo);

        FruitFetcher fruitFetcher = new FruitFetcher(
                mockFruitService,
                mockCallProcessor,
                logger,
                WorkMode.SYNCHRONOUS);
        fruitFetcher.addObserver(mockObserver);


        //act
        fruitFetcher.fetchFruits(mockSuccessCallBack, mockFailureCallbackWithPayload);


        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }


}
