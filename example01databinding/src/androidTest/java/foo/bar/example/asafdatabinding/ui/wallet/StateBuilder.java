package foo.bar.example.asafdatabinding.ui.wallet;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import foo.bar.example.asafdatabinding.CustomApp;
import foo.bar.example.asafdatabinding.feature.wallet.Wallet;

import static org.mockito.Mockito.when;

/**
 *
 */
public class StateBuilder {

    private Wallet mockWallet;

    StateBuilder(Wallet mockWallet) {
        this.mockWallet = mockWallet;
    }

    StateBuilder withMobileWalletMaximum(int totalFundsAvailable) {
        when(mockWallet.getMobileWalletAmount()).thenReturn(totalFundsAvailable);
        when(mockWallet.getSavingsWalletAmount()).thenReturn(0);
        when(mockWallet.canDecrease()).thenReturn(true);
        when(mockWallet.canIncrease()).thenReturn(false);
        return this;
    }

    StateBuilder withMobileWalletHalfFull(int savingsWalletAmount, int mobileWalletAmount) {
        when(mockWallet.getMobileWalletAmount()).thenReturn(mobileWalletAmount);
        when(mockWallet.getSavingsWalletAmount()).thenReturn(savingsWalletAmount);
        when(mockWallet.canDecrease()).thenReturn(true);
        when(mockWallet.canIncrease()).thenReturn(true);
        return this;
    }

    StateBuilder withMobileWalletEmpty(int totalFundsAvailable) {
        when(mockWallet.getMobileWalletAmount()).thenReturn(0);
        when(mockWallet.getSavingsWalletAmount()).thenReturn(totalFundsAvailable);
        when(mockWallet.canDecrease()).thenReturn(false);
        when(mockWallet.canIncrease()).thenReturn(true);
        return this;
    }


    ActivityTestRule<WalletsActivity>  createRule(){

        return new ActivityTestRule<WalletsActivity>(WalletsActivity.class) {
            @Override
            protected void beforeActivityLaunched() {

                //get hold of the application
                CustomApp customApp = (CustomApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
                customApp.injectSynchronousObjectGraph();

                //inject our mocks so our UI layer will pick them up
                customApp.injectMockObject(Wallet.class, mockWallet);
            }

        };
    }

}
