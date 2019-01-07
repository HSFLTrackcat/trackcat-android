package de.mobcom.group3.gotrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.karan.churi.PermissionManager.PermissionManager;

import de.mobcom.group3.gotrack.Dashboard.DashboardFragment;
import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.InExport.Import;
import de.mobcom.group3.gotrack.RecordList.RecordListFragment;
import de.mobcom.group3.gotrack.Recording.Locator;
import de.mobcom.group3.gotrack.Recording.RecordFragment;
import de.mobcom.group3.gotrack.Settings.CustomSpinnerAdapter;
import de.mobcom.group3.gotrack.Settings.SettingsFragment;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private PermissionManager permissionManager = new PermissionManager() {
    };
    final int NOTIFICATION_ID = 100;
    private DrawerLayout mainDrawer;
    private NavigationView navigationView;
    private static MainActivity instance;
    private RecordFragment recordFragment;
    private NotificationManagerCompat notificationManager;
    private Boolean shouldRestart = false;
    public Boolean firstRun=false;
    private static Spinner spinner;
    private static int activeUser;
    private static boolean hints;
    private static boolean darkTheme;
    private static boolean createInitialUser = false;
    private UserDAO userDAO;
    public static Boolean isActiv = false;
    private static final String PREF_DARK_THEME = "dark_theme";

    private static boolean isRestart = false;

    // Restart activity for Theme Switching
    public static void restart() {
        Bundle temp_bundle = new Bundle();
        getInstance().onSaveInstanceState(temp_bundle);
        Intent intent = new Intent(getInstance(), MainActivity.class);
        intent.putExtra("bundle", temp_bundle);

        isRestart = true;

        getInstance().startActivity(intent);
        getInstance().finish();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static Spinner getSpinner() {
        return spinner;
    }

    public static int getActiveUser() {
        return activeUser;
    }

    public static boolean getHints() {
        return hints;
    }

    public static void setHints(boolean activeHints) {
        hints = activeHints;
    }

    public static boolean getDarkTheme() {
        return darkTheme;
    }

    public static void setDarkTheme(boolean activeDarkTheme) {
        darkTheme = activeDarkTheme;
    }

    public static void setCreateUser(boolean createUser) {
        createInitialUser = createUser;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        if (action != null && action.equalsIgnoreCase(getResources().getString(R.string.fRecord))) {
            loadRecord();
        } else if (action != null && action.equalsIgnoreCase(getResources().getString(R.string.fSettings))) {
            loadSettings();
        }
    }

    @Override
    protected void onDestroy() {
        /* Entferne die Benachrichtigung, wenn App läuft */
        notificationManager.cancel(getNOTIFICATION_ID());

        try {
            recordFragment.stopTimer();
            recordFragment = null;
        } catch (NullPointerException e) {

        }
        MainActivity.getInstance().stopService(new Intent(MainActivity.getInstance(), Locator.class));

        isActiv = false;

        if (!isRestart) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        isRestart = false;

        super.onDestroy();
    }

    @Override
    public void recreate() {
        if (Build.VERSION.SDK_INT >= 11) {
            super.recreate();
        } else {
            startActivity(getIntent());
            finish();
        }
    }

    public void getCurrentUserInformation(){
        userDAO = new UserDAO(this);
        List<User> users = userDAO.readAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).isActive()) {
                activeUser = users.get(i).getId();
                hints = users.get(i).isHintsActive();
                darkTheme = users.get(i).isDarkThemeActive();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Fragt nach noch nicht erteilten Permissions */
        permissionManager.checkAndRequestPermissions(this);

        getCurrentUserInformation();
        /* Aktuelles Themes aus Einstellungen laden */
        setTheme(getDarkTheme() ? R.style.AppTheme_Dark : R.style.AppTheme);

        if (getIntent().hasExtra("bundle") && savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras().getBundle("bundle");
        }

        /* Startseite definieren */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isActiv) {
            Toast.makeText(this, "Die App läuft bereits in einer anderen Instanz",
                    Toast.LENGTH_LONG).show();
            finish();
        } else {
            isActiv = true;
        }
        /* Instanz für spätere Objekte speichern */
        instance = this;
        recordFragment = new RecordFragment();
        mainDrawer = findViewById(R.id.drawer_layout);

        /* Actionbar definieren und MenuListener festlegen */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Menu Toggle */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainDrawer.addDrawerListener(toggle);
        toggle.syncState();

        notificationManager = NotificationManagerCompat.from(this);

        /* Initiale Usererstellung */
        userDAO = new UserDAO(this);
        List<User> userList = userDAO.readAll();
        if (userList.size() == 0) {
            User initialUser = new User("Max", "Mustermann", "max.mustermann@mail.de",
                    null);
            initialUser.setActive(true);
            initialUser.setHintsActive(true);
            userDAO.create(initialUser);
            createInitialUser = true;
        }
        firstRun=true;

        spinner = navigationView.getHeaderView(0).findViewById(R.id.profile_spinner);
        addItemsToSpinner();

        /* Startseite festlegen - Erster Aufruf */
        loadDashboard();
    }

    /* Dynamisches Hinzufügen von Spinner-Items */
    public void addItemsToSpinner() {

        /* Erstellen der Listen */
        final ArrayList<byte[]> spinnerAccountIcons = new ArrayList<byte[]>();
        ArrayList<String> spinnerAccountEmail = new ArrayList<String>();
        final ArrayList<String> spinnerAccountNames = new ArrayList<String>();
        List<User> users = userDAO.readAll();
        int selectedID = 0;
        boolean findActiveUser = false;
        for (int i = 0; i < users.size(); i++) {
            spinnerAccountEmail.add(users.get(i).getMail());
            spinnerAccountNames.add(users.get(i).getFirstName() + " " + users.get(i).getLastName());
            spinnerAccountIcons.add(users.get(i).getImage());
            if (users.get(i).isActive()) {
                activeUser = users.get(i).getId();
                hints = users.get(i).isHintsActive();
                darkTheme = users.get(i).isDarkThemeActive();
                selectedID = i;
                findActiveUser = true;
            }
        }

        /*Wenn nach dem Löschen eines Users kein neuer aktiver Nutzer gefunden wurde*/
        if (!findActiveUser) {
            activeUser = users.get(selectedID).getId();
            User newActiveUser = userDAO.read(activeUser);
            newActiveUser.setActive(true);
            userDAO.update(activeUser, newActiveUser);
        }
        final boolean deactivateOldUser = findActiveUser;

        /* Erstellen des Custom Spinners */
        final CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), spinnerAccountIcons, spinnerAccountNames, spinnerAccountEmail);

        /* Setzen des Adapters */
        spinner.setAdapter(spinAdapter);
        spinner.setSelection(selectedID);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {

                /* Überprüfung, ob immoment ein Import aktiv ist */
                if (Import.getImport().getIsImportActiv()) {
                    if (hints) {
                        Toast.makeText(getApplicationContext(), "Nutzerwechsel nicht möglich, da im Moment ein Import läuft.", Toast.LENGTH_LONG).show();
                    }
                } else {

                    /* Auslesen des angeklickten Items */
                    String item = adapter.getItemAtPosition(position).toString();

                    /* Wechseln des Profilbildes */
                    byte[] imgRessource = spinnerAccountIcons.get(position);
                    de.hdodenhof.circleimageview.CircleImageView circleImageView = findViewById(R.id.profile_image);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.raw.default_profile);
                    if (imgRessource != null && imgRessource.length > 0) {
                        bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
                    }
                    circleImageView.setImageBitmap(bitmap);

                    boolean oldUserTheme = true;
                    /* Durchlaufen der Nutzer */
                    List<User> users = userDAO.readAll();
                    for (int i = 0; i < users.size(); i++) {
                        if (adapter.getItemAtPosition(position).equals(users.get(i).getFirstName() + " " + users.get(i).getLastName())) {
                            /* Ausgewählten Nutzer als aktiven Nutzer setzen */
                            User user = userDAO.read(users.get(i).getId());
                            user.setActive(true);
                            userDAO.update(user.getId(), user);

                            /* Alten Nutzer deaktivieren */
                            if (deactivateOldUser && !createInitialUser && !firstRun) {
                                Log.d("TEST123", "IN SCHLEIFE");
                                User oldUser = userDAO.read(activeUser);
                                oldUser.setActive(false);
                                userDAO.update(activeUser, oldUser);
                            } else {
                                createInitialUser = false;
                                firstRun=false;
                            }

                            /* Nutzerwechsel in globaler Variable */
                            activeUser = users.get(i).getId();
                            hints = users.get(i).isHintsActive();

                            oldUserTheme = darkTheme;
                            darkTheme = users.get(i).isDarkThemeActive();
                            if (hints) {
                                Toast.makeText(getApplicationContext(), "Ausgewähltes Profil: " + item, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    /*Anzeigen des Dashboard nach Wechsel des Nutzers*/
                    loadDashboard();
                    Menu menu = navigationView.getMenu();
                    menu.findItem(R.id.nav_dashboard).setChecked(true);

                    if (shouldRestart) {
                        shouldRestart = false;
                        if (oldUserTheme != darkTheme) {
                            MainActivity.isActiv = false;
                            restart();
                        }
                    } else {
                        shouldRestart = true;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_help, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.nav_help:
                Toast.makeText(getApplicationContext(), "Hilfe anzeigen", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        /* Aktion je nach Auswahl des Items */
        switch (menuItem.getItemId()) {
            case R.id.nav_dashboard:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard)) == null) {
                    loadDashboard();
                }
                break;
            case R.id.nav_recordlist:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist)) == null) {
                    loadRecordList();
                }
                break;
            case R.id.nav_record:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord)) == null) {
                    loadRecord();
                }
                break;
            case R.id.nav_import:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(
                        Intent.createChooser(intent, "Import"), 0);
                break;
            case R.id.nav_settings:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fSettings)) == null) {
                    loadSettings();
                }
                break;
        }
        menuItem.setChecked(true);
        mainDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        File file = new File(getCacheDir(), "document");
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Import.getImport().handleSend(this, file, inputStream);
                        addItemsToSpinner();
                        loadDashboard();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* Stops/pauses Tracking opens App and switch to RecordFragment */
    public void stopTracking() {
        startActivity(getIntent());
        try {
            loadRecord();
        } catch (RuntimeException e) {
            Log.v("Fehler beim Stoppen: ", e.toString());
        }
        recordFragment.stopTracking();

    }

    public int getNOTIFICATION_ID() {
        return NOTIFICATION_ID;
    }

    public void startTracking() {
        recordFragment.startTracking();
        startActivity(getIntent());
        try {
            loadRecord();
        } catch (RuntimeException e) {

        }
    }

    /* Startet RecordFragment nach Ende der Aufzeichnung */
    public void endTracking() {
        recordFragment = new RecordFragment();
        loadRecord();
    }

    public RecordFragment getRecordFragment() {
        return recordFragment;
    }

    /* BackPressed Listener */
    private boolean exitApp = false;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetailsDashbaord)) != null) {
            loadDashboard();
        } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetailsList)) != null) {
            loadRecordList();
        } else {
            if (exitApp) {
                finish();
                System.exit(0);
            }

            exitApp = true;
            if (hints) {
                Toast.makeText(instance, "Noch einmal klicken, um App zu beenden!", Toast.LENGTH_SHORT).show();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitApp = false;
                    if (hints) {
                        Toast.makeText(instance, "Zu langsam. Versuche es erneut...", Toast.LENGTH_LONG).show();
                    }
                }
            }, 3000);
        }
    }

    /* Laden des Dashboard-Fragments */
    public void loadDashboard() {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new DashboardFragment(), getResources().getString(R.string.fDashboard));
        fragTransaction.commit();
    }

    /* Laden des Aufnahme-Fragments */
    public void loadRecord() {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, recordFragment, getResources().getString(R.string.fRecord));
        fragTransaction.commit();
    }

    /* Laden des Listen-Fragments */
    public void loadRecordList() {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new RecordListFragment(), getResources().getString(R.string.fRecordlist));
        fragTransaction.commit();
    }

    /* Laden des Einstellung-Fragments */
    public void loadSettings() {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new SettingsFragment(), getResources().getString(R.string.fSettings));
        fragTransaction.commit();
    }

    // set the RecordFragment wich is in use
    public void setRecordFragment(RecordFragment recordFragment) {
        this.recordFragment = recordFragment;
    }
}
