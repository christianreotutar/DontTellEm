package com.example.tictactoe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


public class HideActivity extends Activity {

    private static final String TAG_LOW = "CS_Debug_LOW";
    private static final String TAG_MED = "CS_Debug_MED";
    
    private static final File picsDir = Environment.
                getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
    
    private static final File camDir = Environment.
                getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM);
    
    private static final File docDir = Environment.
            getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS);
    
    private static final File downDir = Environment.
            getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
    
    private static final File voiceDir = new File(
            Environment.getExternalStorageDirectory(), "Sounds");
    
    
    private static final File[] hideDirs = {picsDir, camDir};//, docDir, downDir, voiceDir};
    
    
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide);
        ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progress);
        
        /*File castle = new File(picsDir, "Castle.jpg");
        File city = new File(picsDir, "City.jpg");*/
        
        /*File temp = new File(picsDir, "Temp.jpg");
        castle.renameTo(temp);
        scanFor(castle.getAbsolutePath());*/
        
        //scanFor(city.getAbsolutePath());
        
        /*city.renameTo(castle);
        scanFor(castle.getAbsolutePath());*/
        
        
        //temp.renameTo(city);
        
        /*File A = new File(picsDir, "A.jpg");
        File B = new File(picsDir, "B.jpg");

        try {
            if (!A.exists()) {
                
                    A.createNewFile();
                    copyFile(castle, A);
                
            }
            if (!B.exists()) {
                
                B.createNewFile();
                copyFile(city, B);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        
        
        int request_code = getIntent().getIntExtra("REQUEST_CODE", 0);
        boolean hidden = false;
        String message = "ERROR";
        if (request_code == MainActivity.RESULT_HIDE) {
        	boolean result = hideAll();
            
            if (result) message = "Files successfully hidden";
            else message = "Error while hiding files";

        } else if (request_code == MainActivity.RESULT_UNHIDE){
        	boolean result = unhideAll();
            
            if (result) message = "Files successfully restored";
            else message = "Error while restoring files";
        }
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);  
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        int id = item.getItemId();
        if (id == R.id.action_settings) {
               return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
   
    /**
     * Hide all images, documents, voice files, etc. on the phone.
     * 
     * @return True if success, false if failure.
     */
    public boolean hideAll() {

        for (int i = 0; i < hideDirs.length; i++) {
            File hideDir = hideDirs[i];
            if (!hideDir.exists()) {
                /* Toast feedback = Toast.makeText(getApplicationContext(), "No " + hideDir.getName() + " directory!", 1);
                feedback.show();*/
                Log.d(TAG_MED, "No " + hideDir.getName() + " directory");
            } else if (hideDir.equals(camDir)) { 
                hideCamWithDummy();
            } else {
                //String[] paths = listContainedFilePaths(hideDir);
                boolean result = hideDir.renameTo(dot(hideDir));
                if (!result) {
                    Log.d(TAG_MED, "Failed to rename " + hideDir.getName() + " directory");
                }
                //scanFor(paths);
            }
        }
        
        File nomedia = new File(Environment.getExternalStorageDirectory(), ".nomedia");
        try {
			nomedia.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        //signOut();
        //deleteContacts();
        clearCache();

        return true;
    }
    
    /**
     * Restore all images on the phone from their hidden
     * state.
     * 
     * @return True if success, false if failure.
     */
    public boolean unhideAll() {
        
        for (int i = 0; i < hideDirs.length; i++) {
            File hideDir = hideDirs[i];
            
            if (!dot(hideDir).exists()) {
                Log.d(TAG_MED, "Couldn't find " + dot(hideDir).getName());
            } else if (hideDir.equals(camDir)) {
                unhideCamWithDummy();
            } else {
                boolean result = dot(hideDir).renameTo(hideDir);
                if (!result) {
                    Log.d(TAG_MED, "Failed to rename" + dot(hideDir).getName());
                } else {
                    scanDir(hideDir);
                }
            }
        }
        
        File nomedia = new File(Environment.getExternalStorageDirectory(), ".nomedia");
        nomedia.delete();
        
        return true;
    }
    
    
    /**
     * Hide camera photos with dummy photos of the same name
     * 
     * @return True if success, false if failure.
     */
    public boolean hideCamWithDummy() {
        // Check if already hidden: Proceed only if dummy folder exists
        File dummyDir = new File(Environment.getExternalStorageDirectory(), "Dummy");
        if (!dummyDir.exists()) {
            Log.d(TAG_MED, "No dummy folder found; assuming DCIM is already hidden.");
            return false;
        }
        
        // Get list of files in 'DCIM', excluding '.thumbnails'
        ArrayList<File> photos = this.listContainedFiles(camDir);
        
        // Rename 'DCIM' to '.DCIM'
        boolean result = camDir.renameTo(dot(camDir));
        if (!result) {
            Log.d(TAG_MED, "Failed to rename " + camDir.getName() + " directory");
            return false;
        }
        
        // Starting to recreate 'DCIM' from dummy files.
        dummyDir.renameTo(camDir);
        
        // Get list of files in what was originally 'dummy'
        ArrayList<File> dummies = this.listContainedFiles(camDir);
        
        // Assert that there are fewer dummy files than there are DCIM files, or
        // there's an equal number of both
        if (!(dummies.size() <= photos.size())) {
            Log.d(TAG_MED, "FAIL: No. dummy photos exceeds no. camera photos.");
            return false;
        }

        
        // Rename each of the dummy photos to the name of one of
        // the preexesting camera photos
        for (int i = 0; i < dummies.size(); i++) {
            File dummy = dummies.get(i);
            File photo = photos.get(i);
            
            // Create directory that dummy will be moved to
            File photoParent = photo.getParentFile();
            if (!photoParent.exists()) {
                boolean makeParent = photoParent.mkdirs();
                if (!makeParent) {
                    Log.d(TAG_MED, "Failed to make parent dir " + photoParent.getAbsolutePath());
                    return false;
                }
            }
            
            // Place dummy in appropriate directory, with appropriate name
            File dummyParent = dummy.getParentFile();
            if (dummyParent.equals(photoParent)) {
                // Rename dummy to name of preexisting photo, if possible
                boolean renameDummy = dummy.renameTo(photo);
                if (!renameDummy) {
                    Log.d(TAG_MED, "Failed to rename dummy " + dummy.getAbsolutePath() + " to " + photo.getAbsolutePath() );
                    return false;
                }
            } else {
                // Otherwise try to move the dummy to the preexisting photo's location
                try {
                    photo.createNewFile();
                    copyFile(dummy, photo);
                } catch (IOException e) {
                    Log.d(TAG_MED, "Failed to create/copy to new file while moving "
                            + dummy.getAbsolutePath() + " to " + photo.getAbsolutePath() );
                    return false;
                }
                
                boolean deleteMovedDummy = dummy.delete();
                if (!deleteMovedDummy) {
                    Log.d(TAG_MED, "Failed to delete old dummy while moving "
                            + dummy.getAbsolutePath() + " to " + photo.getAbsolutePath() );
                    return false;
                }
            }
            
            // Scan in the dummy file
            scanFor(photo.getAbsolutePath());
        } 
        
        return true;
    }
    
    /**
     * Restore camera photos that were hidden with dummy photos
     * of the same name.
     * 
     * @return True if success, false if failure.
     */
    public boolean unhideCamWithDummy() {
        // Check if already restored: Proceed only if dummy folder DOES NOT exist
        File dummyDir = new File(Environment.getExternalStorageDirectory(), "Dummy");
        if (dummyDir.exists()) {
            Log.d(TAG_MED, "Dummy folder found; assuming DCIM is already restored.");
            return false;
        }
        
        // Rename folders to return dummies to dummy folders and photos to DCIM folder
        boolean renameCam = camDir.renameTo(dummyDir);
        boolean renameDotCam = dot(camDir).renameTo(camDir);
        if (!renameCam) {
            Log.d(TAG_MED, "Failed to rename " + camDir.getAbsolutePath() + " to " + dummyDir.getAbsolutePath() );
            return false;
        }
        
        if (!renameDotCam) {
            Log.d(TAG_MED, "Failed to rename " + dot(camDir).getAbsolutePath() + " to " + camDir.getAbsolutePath() );
            return false;
        } else {
            scanDir(camDir);
        }
        
        return true;
    }
    
    /**
     * Sign out of Google account
     */
    public void signOut() {
        AccountManager manager = AccountManager.get(this);
        // Account[] accounts = manager.getAccountsByType("com.google");
        Account[] accounts = manager.getAccounts();

        for (int i = 0; i < accounts.length; i++) {
            manager.removeAccount(accounts[i], null, null);
        }
    }
    
    /**
     * Delete contacts from phone; can be restored later from Google account (?)
     */
    public void deleteContacts() {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            contentResolver.delete(uri, null, null);
        }
    }
    
    /**
     * Restore deleted contacts (from Google account?)
     */
    private void reloadContacts() {
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();

        for (Account a : accounts)
        {
            int canSync = ContentResolver.getIsSyncable(a, ContactsContract.AUTHORITY);

            if (canSync > 0)
            {
                Bundle extras = new Bundle();
                extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                ContentResolver.requestSync(accounts[0], ContactsContract.AUTHORITY, extras);
            }
        }
    }
    
    /**
     * Clear cache (for all apps?). Gets Gallery to update faster.
     */
    public void clearCache() {
    	PackageManager  pm = getPackageManager();
    	// Get all methods on the PackageManager
    	Method[] methods = pm.getClass().getDeclaredMethods();
    	for (Method m : methods) {
    	    if (m.getName().equals("freeStorageAndNotify")) {
    	        // Found the method I want to use
    	        try {
    	            long desiredFreeStorage = Long.MAX_VALUE; // Request for 8GB of free space
    	            m.invoke(pm, desiredFreeStorage , null);
    	        	Log.d("CC Success", "cache cleared");
    	        } catch (Exception e) {
    	            // Method invocation failed. Could be a permission problem
    	        	Log.d("CC Failure", "Could not clear cache");
    	        }
    	        break;
    	    }
    	}	
    }
    
    
    
    
    /**
     * Recursively build a list of all files inside a directory, and
     * inside the subdirectories of that directory.
     * 
     * @param dir   Directory containing the desired files.
     * @return      List of Files representing each file. Directories
     *              aren't included in this list.
     */
    private ArrayList<File> listContainedFiles(File dir) {
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(dir.listFiles()));
        
        int i = 0;
        while (i < files.size()) {
            File file = files.get(i);
            
            if (file.isDirectory() ) {
                if (file.getName().charAt(0) != '.')
                    files.addAll(listContainedFiles(file));
                files.remove(i);
            } else i++;
        }
        
        return files;
    }
    
    /**
     * Recursively build a list of all files inside a directory, and
     * inside the subdirectories of that directory. Return the paths
     * of those files.
     * 
     * @param dir   Directory containing the desired files.
     * @return      Paths of the discovered files.
     */
    private String[] listContainedFilePaths(File dir) {
        ArrayList<File> files = listContainedFiles(dir);
        
        String[] paths = new String[files.size()];
        for (int i = 0; i < files.size(); i++) {
            paths[i] = files.get(i).getAbsolutePath();
        }
        
        return paths;
    }
    
    /**
     * Have the MediaScanner scan every file in a given
     * directory, and in every subdirectory of that directory.
     * Gets gallery to update faster (but apparently only
     * when *restoring*, not deleting/hiding, files).
     * 
     * @param dir   Directory to search.
     */
    private void scanDir(File dir) {
        if (!dir.exists())
            return;
        
        final String[] paths = listContainedFilePaths(dir);
        scanFor(paths);
    }
    
    /**
     * Have the MediaScanner scan at the specified locations.
     * Have the MediaScanner scan at the specified locations.
     * Gets gallery to update faster (but apparently only
     * when *restoring*, not deleting/hiding, files).
     * 
     * @param paths The locations at which to scan.
     */
    private void scanFor(final String[] paths) {        
        MediaScannerConnection.scanFile(HideActivity.this, paths, null,  new OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                
                final String p = path;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message = "Finished scanning: " + new File(p).getAbsolutePath();
                        Log.d(TAG_LOW, message);
                        /*Toast feedback = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                        feedback.show();*/
                    }
                });
            }
        });
    }
    
    /**
     * Have the MediaScanner scan at the specified locations.
     * Gets gallery to update faster (but apparently only
     * when *restoring*, not deleting/hiding, files).
     * 
     * @param path The location at which to scan.
     */
    private void scanFor(final String path) {
        String[] paths = { path };
        scanFor(paths);
        
        return;
    }
    
    /**
     * Given an unhidden file, make a 'hidden' version of it by
     * putting a '.' at the beginning.
     * 
     * @param file  File to be hidden
     * @return The hidden version of the file.
     */
    private File dot(File file) {
         return new File(file.getParentFile(), "." + file.getName());
    }
    
    
    /* ************************
     * UNUSED STUFF -- MAYBE DELETE
     * ************************ */
    /**
     * Copy one file to another.
     * 
     * @param source    File to copy from.
     * @param dest      File to copy to.
     * @throws IOException
     */
    private static void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }
    
    private ArrayList<File> listContainedDirs(File dir) {
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(dir.listFiles()));
        ArrayList<File> dirs = new ArrayList<File>();

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            
            if (file.isDirectory() && file.getName().charAt(0) != '.') {
                dirs.add(file);
                dirs.addAll(listContainedDirs(file));
            }
        }
        
        return dirs;
    }
    
    private String[] listContainedDirPaths(File dir) {
        ArrayList<File> dirs = listContainedDirs(dir);
        
        String[] paths = new String[dirs.size()];
        for (int i = 0; i < dirs.size(); i++) {
            paths[i] = dirs.get(i).getAbsolutePath();
        }
        
        return paths;
    }
     
}
