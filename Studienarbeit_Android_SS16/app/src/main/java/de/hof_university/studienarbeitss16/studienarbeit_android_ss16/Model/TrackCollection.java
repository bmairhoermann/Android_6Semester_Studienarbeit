package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brianmairhoermann on 13.06.16.
 */
public class TrackCollection implements Serializable{

    public List<TrackModel> trackCollectionList = new ArrayList<>();

}
