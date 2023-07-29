package com.papaworx.cpro.utilities;

import com.papaworx.cpro.model.Person;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class CpBoolean extends BooleanProperty{
	private BooleanProperty bp;
	private Boolean changed = false;
	private String Name;
	private Person Owner;
	private boolean isNull;
	
	public CpBoolean(Boolean b, String name, Person owner) {
		bp = new SimpleBooleanProperty(b);
		Owner = owner;
		bp.addListener(new ChangeListener<Boolean>() {
			
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				changed = true;
				Owner.setChange(true);
			}
		});
		changed = false;
		Name = name;
	}
	@Override
	public void bind(ObservableValue<? extends Boolean> observable) {
		// TODO Auto-generated method stub
		bp.bind(observable);
	}

	@Override
	public void unbind() {
		// TODO Auto-generated method stub
		bp.unbind();
	}

	@Override
	public boolean isBound() {
		// TODO Auto-generated method stub
		return bp.isBound();
	}

	@Override
	public Object getBean() {
		// TODO Auto-generated method stub
		return bp.getBean();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Name;
	}

	@Override
	public void addListener(ChangeListener<? super Boolean> listener) {
		// TODO Auto-generated method stub
		bp.addListener(listener);
	}

	@Override
	public void removeListener(ChangeListener<? super Boolean> listener) {
		// TODO Auto-generated method stub
		bp.removeListener(listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		bp.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		bp.removeListener(listener);
	}

	@Override
	public boolean get() {
		// TODO Auto-generated method stub
		return bp.get();
	}

	@Override
	public void set(boolean value) {
		// TODO Auto-generated method stub
		bp.set(value);
	}

	public Boolean hasChanged() {
		return changed;
	}
	
	public void prime() {
		changed = false;
	}
	
	public void smudge() {
		changed = true;
	}
	
	public boolean isNull() {
		return isNull;
	}
	
	/*public Boolean getValue() {
		if (isNull)
			return null;
		else
			return bp.getValue();
	}*/
}
