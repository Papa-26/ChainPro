package com.papaworx.cpro.utilities;

import com.papaworx.cpro.model.Person;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class CpString extends StringProperty {
	private StringProperty sp;
	private Boolean changed;			// whether object value has changed
	private String Name;				// Object name
	private Person Owner;

	public CpString(String s, String name, Person owner) {
		sp = new SimpleStringProperty(s);
		Owner = owner;
		sp.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				changed = true;
				Owner.Change();
			}
		});
		changed = false;
		Name = name;
	}
	
	@Override
	public void bind(ObservableValue<? extends String> observable) {
		// TODO Auto-generated method stub
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
		String s = sp.get();
		if (s != null)
			return s;
		else
			return "";
	}

	@Override
	public void set(String value) {
		sp.set(value);
	}

	public Boolean hasChanged() {
		return changed;
	}
	
	public Boolean is_Empty() {
		if (sp == null)
			return true;
		else
			return sp.get().isEmpty();
	}
	
	public void prime() {
		changed = false;
	}
	
	public void smudge() {
		changed = true;
	}
}
