package android.example.com.squawker.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by tareq on 4/12/17.
 */

public class SquawkerFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = SquawkerFirebaseInstanceIdService.class.getSimpleName();
    
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onTokenRefresh: Refresh Token, "+ refreshedToken);
        
        sendRegistrationToServer(refreshedToken);      
    }

    private void sendRegistrationToServer(String refreshedToken) {
    }
}
