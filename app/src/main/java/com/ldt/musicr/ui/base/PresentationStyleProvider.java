package com.ldt.musicr.ui.base;

import java.util.HashMap;

/**
 * A PresentationProvider stores a set of {@link PresentationStyle}s that provides valid ways to present a {@link PresentationFragment}
 */
public class PresentationStyleProvider {
    private static final HashMap<Class<?>, String> sNames = new HashMap<>();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    private final HashMap<String, PresentationStyle> mStyles = new HashMap<>();

    public PresentationStyle get(String name) {
        if(!validateName(name)) {
            throw new IllegalArgumentException("style name cannot be an empty string");
        }

        PresentationStyle style = mStyles.get(name);
        if (style == null) {
            throw new IllegalStateException("Could not find Style with name \"" + name
                    + "\". You must call PresentationProvider.add() for each presentation style type.");
        }
        return style;
    }

    public PresentationStyle addStyle(String name, PresentationStyle styleController) {
        if (!validateName(name)) {
            throw new IllegalArgumentException("style name cannot be an empty string");
        }
        return mStyles.put(name, styleController);
    }

    public PresentationStyle addStyle(PresentationStyle style) {
        String name = style.getName();
        return addStyle(name, style);
    }
}
