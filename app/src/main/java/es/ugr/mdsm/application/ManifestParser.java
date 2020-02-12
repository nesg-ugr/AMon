package es.ugr.mdsm.application;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import fr.xgouchet.axml.CompressedXmlParser;

public class ManifestParser {
    private static String TAG = "Amon.ManifestParser";

    // https://stackoverflow.com/a/25474188
    public static Document extractManifest(ApplicationInfo applicationInfo){
        Document dom = null;
        try{
            ZipFile apk = new ZipFile(applicationInfo.publicSourceDir);
            ZipEntry manifest = apk.getEntry("AndroidManifest.xml");
            if (manifest != null){
                InputStream stream = apk.getInputStream(manifest);
                dom = new CompressedXmlParser().parseDOM(stream);
                stream.close();
            }
            apk.close();
        } catch (IOException e){
            Log.e(TAG, "Failed to extract manifest from "+ applicationInfo.name);
        } catch (ParserConfigurationException e){
            Log.e(TAG, "Failed to decode AXML");
        }

        return dom;
    }
}
