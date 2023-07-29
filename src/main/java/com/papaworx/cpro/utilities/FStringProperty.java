package com.papaworx.cpro.utilities;

import com.papaworx.cpro.model.Family;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class FStringProperty extends StringProperty {
	private StringProperty sp;
	private Boolean changed;			// whether object value has changed
	private String Name;				// Object name
	private Family Owner;

	public FStringProperty(String s, String name, Family owner) {
		sp = new SimpleStringProperty(s);
		Owner = owner;
		sp.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				changed = true;
				Owner.Change(true);
			}
		});
		changed = false;
		Name = name;
	}

	@Override
	public void bind(ObservableValue<? extends String> observable) {
		sp.bind(observable);
	}

	@Override
	public void unbind() {
		sp.unbind();
	}

	@Override
	public boolean isBound() {
		return sp.isBound();
	}

	@Override
	public Object getBean() {
		return sp.getBean();
	}

	@Override
	public String getName() {
		return Name;
	}

	@Override
	public void addListener(ChangeListener<? super String> listener) {
		sp.addListener(listener);
		
	}

	@Override
	public void removeListener(ChangeListener<? super String> listener) {
		sp.removeListener(listener);
		
	}

	@Override
	public void addListener(InvalidationListener listener) {
		sp.addListener(listener);
		
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		sp.removeListener(listener);
		
	}

	@Override
	public String get() {
		return sp.get();
	}

	@Override
	public void set(String value) {
		// TODO Auto-generated method stub
		sp.set(value);
	}

	public Boolean hasChanged() {
		return changed;
	}
	
	public void prime() {
		changed = false;
	}
}
