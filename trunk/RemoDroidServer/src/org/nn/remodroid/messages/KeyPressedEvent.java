package org.nn.remodroid.messages;

public class KeyPressedEvent extends AbstractRemoteMessage {

	private static final long serialVersionUID = 1L;

	private final char keyChar;
	private final boolean shiftPressed;
	private final boolean ctrlPressed;
	private final boolean altPressed;
	
	public KeyPressedEvent(char keyChar) {
		this(keyChar, false, false, false);
	}

	public KeyPressedEvent(char keyChar, boolean shiftPressed) {
		this(keyChar, shiftPressed, false, false);
	}
	
	public KeyPressedEvent(char keyChar, boolean shiftPressed, boolean ctrlPressed, boolean altPressed) {
		this.keyChar = keyChar;
		this.shiftPressed = shiftPressed;
		this.ctrlPressed = ctrlPressed;
		this.altPressed = altPressed;
	}
	
	public char getKeyChar() {
		return keyChar;
	}
	
	public boolean isShiftPressed() {
		return shiftPressed;
	}
	
	public boolean isCtrlPressed() {
		return ctrlPressed;
	}
	
	public boolean isAltPressed() {
		return altPressed;
	}
}
