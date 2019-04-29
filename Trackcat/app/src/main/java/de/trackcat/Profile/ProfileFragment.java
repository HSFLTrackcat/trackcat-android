package de.trackcat.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileFragment extends Fragment {

    TextView name, email, dayOfBirth, gender, weight, size, bmi, state, lastLogIn, dayOfRegistration;
    CircleImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        boolean loadMenu = getArguments().getBoolean("loadMenu");

        if (loadMenu) {
            /* Inlate Menu */
            MenuInflater menuInflater = MainActivity.getInstance().getMenuInflater();
            menuInflater.inflate(R.menu.profile_settings, MainActivity.getMenuInstance());
        }

        /* get current user */
        UserDAO userDAO = new UserDAO(MainActivity.getInstance());
        User currentUser = userDAO.read(MainActivity.getActiveUser());

        /* get profil fields */
        name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        dayOfBirth = view.findViewById(R.id.user_dayOfBirth);
        gender = view.findViewById(R.id.user_gender);
        weight = view.findViewById(R.id.user_weight);
        size = view.findViewById(R.id.user_size);
        bmi = view.findViewById(R.id.user_bmi);
        state = view.findViewById(R.id.user_state);
        lastLogIn = view.findViewById(R.id.user_lastLogIn);
        dayOfRegistration = view.findViewById(R.id.user_dayOfRegistration);
        image = view.findViewById(R.id.profile_image);

        //TODO read values from global db
        HashMap<String, String> map = new HashMap<>();
        map.put("eMail", currentUser.getMail());

        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        // TODO hashsalt Password
        /* start a call */
        Call<ResponseBody> call = apiInterface.getUserByEmail(map);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                /* get jsonString from API */

                try {
                    String jsonString = response.body().string();

// TODO have fun :P
                    /* parse json */
                    JSONObject userJSON = new JSONObject(jsonString);

                    Log.v("test", userJSON.getString("eMail"));

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();


            }
        });




        /* read values from local DB */
        setProfileValues(currentUser.getFirstName(), currentUser.getLastName(), currentUser.getMail(), currentUser.getDateOfBirth(), currentUser.getSize(), currentUser.getWeight());

        return view;
    }

    private void setProfileValues(String user_firstName, String user_lastName, String user_email, long user_dayOfBirth, float user_size, float user_weight) {


        /*set profile values*/
        name.setText(user_firstName + " " + user_lastName);
        email.setText(user_email);
        ;
        String curDateString = getDate(user_dayOfBirth, "dd.MM.yyyy");
        dayOfBirth.setText(curDateString);

        weight.setText("" + user_weight + " kg");
        size.setText("" + user_size + " cm");

        /* bmi */
        float userSize = user_size;
        boolean userGender = true;
        float userWeight = user_weight;
        float x = (userSize / 100) * (userSize / 100);

        double userBmi = Math.round((userWeight / x) * 100) / 100.0;

        String bmiClass = "nicht angebegen";
        int age = 30;
        /* if user is female */
        if (userGender) {
            /*children*/
            if (age == 8) {
                if (userBmi < 13.2) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 13.3 && userBmi <= 18.7) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 18.8 && userBmi <= 22.2) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 22.3) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 9) {
                if (userBmi < 13.7) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 13.8 && userBmi <= 19.7) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 19.8 && userBmi <= 23.3) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 23.4) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 10) {
                if (userBmi < 14.2) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 14.3 && userBmi <= 20.6) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 20.7 && userBmi <= 23.3) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 23.4) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 11) {
                if (userBmi < 14.6) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 14.7 && userBmi <= 20.7) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 20.8 && userBmi <= 22.8) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 22.9) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 12) {
                if (userBmi < 16) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 16.1 && userBmi <= 21.4) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 21.5 && userBmi <= 23.3) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 23.4) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 13) {
                if (userBmi < 15.6) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 15.7 && userBmi <= 22) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 22.1 && userBmi <= 24.3) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 24.4) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 14) {
                if (userBmi < 17) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 17.1 && userBmi <= 23.1) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 23.2 && userBmi <= 25.9) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 26) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 15) {
                if (userBmi < 17.6) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 17.7 && userBmi <= 23.1) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 23.2 && userBmi <= 27.5) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 27.6) {
                    bmiClass = "starkes Übergewicht";
                }
            }

            /*adults*/
            else if (age == 16) {
                if (userBmi < 19) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 19 && userBmi <= 24) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 25 && userBmi <= 28) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 28) {
                    bmiClass = "starkes Übergewicht";
                }
            } else if (age >= 17 && age <= 24) {
                if (userBmi < 20) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 20 && userBmi <= 25) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 26 && userBmi <= 29) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 29) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age >= 25 && age <= 34) {
                if (userBmi < 21) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 21 && userBmi <= 26) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 27 && userBmi <= 30) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 30) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age >= 35 && age <= 44) {
                if (userBmi < 22) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 22 && userBmi <= 27) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 28 && userBmi <= 31) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 31) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age >= 45 && age <= 54) {
                if (userBmi < 23) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 23 && userBmi <= 28) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 29 && userBmi <= 32) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 32) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age >= 55 && age <= 64) {
                if (userBmi < 24) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 24 && userBmi <= 29) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 30 && userBmi <= 33) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 33) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age > 65) {
                if (userBmi < 25) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 25 && userBmi <= 30) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 31 && userBmi <= 34) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 34) {
                    bmiClass = "starkes Übergewicht";
                }
            }

            /* if user is male */
        } else {

            /*children*/
            if (age == 8) {
                if (userBmi < 14.2) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 14.3 && userBmi <= 19.2) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 19.3 && userBmi <= 22.5) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 22.6) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 9) {
                if (userBmi < 13.7) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 13.8 && userBmi <= 19.3) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 19.4 && userBmi <= 21.5) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 21.6) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 10) {
                if (userBmi < 14.6) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 14.7 && userBmi <= 21.3) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 21.4 && userBmi <= 24.9) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 25) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 11) {
                if (userBmi < 14.3) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 14.4 && userBmi <= 21.1) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 21.2 && userBmi <= 23.1) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 23.1) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 12) {
                if (userBmi < 14.8) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 14.9 && userBmi <= 21.9) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 22 && userBmi <= 24.7) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 24.8) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 13) {
                if (userBmi < 16.2) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 16.3 && userBmi <= 21.6) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 21.7 && userBmi <= 24.4) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 24.5) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 14) {
                if (userBmi < 16.7) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 16.8 && userBmi <= 22.5) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 22.6 && userBmi <= 25.6) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 25.7) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age == 15) {
                if (userBmi < 18.5) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 18.6 && userBmi <= 23.6) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 23.7 && userBmi <= 25.9) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 26) {
                    bmiClass = "starkes Übergewicht";
                }
            }

            /*adults*/
            else if (age >= 16 && age <= 24) {
                if (userBmi < 19) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 19 && userBmi <= 24) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 25 && userBmi <= 28) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 28) {
                    bmiClass = "starkes Übergewicht";
                }
            } else if (age >= 25 && age <= 34) {
                if (userBmi < 20) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 20 && userBmi <= 25) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 26 && userBmi <= 29) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 29) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age >= 35 && age <= 44) {
                if (userBmi < 21) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 21 && userBmi <= 26) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 27 && userBmi <= 30) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 30) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age >= 45 && age <= 54) {
                if (userBmi < 22) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 22 && userBmi <= 27) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 28 && userBmi <= 31) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 31) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age >= 55 && age <= 64) {
                if (userBmi < 23) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 23 && userBmi <= 28) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 29 && userBmi <= 32) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 32) {
                    bmiClass = "starkes Übergewicht";
                }

            } else if (age > 65) {
                if (userBmi < 24) {
                    bmiClass = "Untergewicht";
                } else if (userBmi >= 24 && userBmi <= 29) {
                    bmiClass = "Normalgewicht";
                } else if (userBmi >= 30 && userBmi <= 33) {
                    bmiClass = "leichtes Übergewicht";
                } else if (userBmi > 33) {
                    bmiClass = "starkes Übergewicht";
                }

            }
        }

        bmi.setText(userBmi + " (" + bmiClass + ")");
        // long curlastLogin = currentUser.getLastLogin();
        // String curDateString = getDate(curDayIfBirth, "dd.MM.yyyy");
        // dayOfBirth.setText(curDateString);

    }

    /* Das Datum wird von Millisekunden als Formatiertes Datum zurückgegeben */
    private static String getDate(long millis, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }
}
