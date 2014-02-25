package com.AridRayne.DreamStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ImageSourcesActivity extends Activity {
	SharedPreferences preferences;
	String[] entries, entryValues;
	String title = "title";
	String uri = "uri";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_sources);
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		entries = preferences.getString("pref_image_source_titles", "Test1,Test2").split(",");
		entryValues = preferences.getString("pref_image_source_titles", "www.test1.com,www.test2.com").split(",");
		ListView lv = (ListView) findViewById(R.id.imageSourcesListView);
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(), entries[position], Toast.LENGTH_LONG).show();
				return true;
			}
			
		});
		lv.setAdapter(createListAdapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.image_sources, menu);
		return true;
	}
	
	private List<Map<String, String>> convertToListItems() {
		List<Map<String, String>> listItem = new ArrayList<Map<String, String>>(entries.length);
		int pos = 0;
		for (String item : entries) {
			Map<String, String> listItemMap = new HashMap<String, String>();
			listItemMap.put(title, item);
			listItemMap.put(uri, entryValues[pos++]);
			listItem.add(Collections.unmodifiableMap(listItemMap));
		}
		return Collections.unmodifiableList(listItem);
	}
	
	private ListAdapter createListAdapter() {
		String[] fromMapKey = new String[] {title, uri};
		int[] toLayoutId = new int[] {android.R.id.text1, android.R.id.text2};
		List<Map<String, String>> list = convertToListItems();
		return new SimpleAdapter(getApplicationContext(), list, android.R.layout.simple_list_item_2, fromMapKey, toLayoutId); 
	}

}
