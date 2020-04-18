package com.example.jukebox;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;

import com.example.jukebox.activity.ChoosePartyActivity;
import com.example.jukebox.adapter.PartiesAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.time.Instant.ofEpochMilli;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class ChoosePartyActivityUnitTest {

    private ChoosePartyActivity testInstance;

    @Mock
    private Context context;

    @Mock
    private PartiesAdapter partiesAdapter;

    @Before
    public void setup() {
        initMocks(this);
        testInstance = new ChoosePartyActivity();
    }

    @Test
    public void displaysAllPartiesSuccessfully() {

    }

    @Test
    public void sendsChosenPartyToDatabase() {

    }

    @Test
    public void shouldRefreshTokenAfterExpiredOnTimeHasPassed() {
        ActivityScenario<ChoosePartyActivity> scenario = ActivityScenario.launch(ChoosePartyActivity.class);
        scenario.onActivity(activity -> {
            SharedPreferences tokenPreferences = activity.getSharedPreferences("token", Context.MODE_PRIVATE);

            Clock constantClock = Clock.fixed(ofEpochMilli(0), ZoneId.systemDefault());

            activity.clock = constantClock;

            tokenPreferences.edit().putLong("expiresOn", epochMilliTimeMinusMinutes(constantClock, 5));

        });

    }

    private long epochMilliTimeMinusMinutes(Clock clock, Integer minutes) {
        return LocalDateTime.now(clock)
                .atZone(ZoneId.systemDefault())
                .minusMinutes(minutes)
                .toInstant()
                .toEpochMilli();
    }
}