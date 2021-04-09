package com.ldt.musicr.ui.widget.avsb;

class Atom {  // note: latest versions of spec simply call it 'box' instead of 'atom'.
    private int mSize;  // includes atom header (8 bytes)
    private int mType;
    private byte[] mData;  // an atom can either contain data or children, but not both.
    private Atom[] mChildren;
    private byte mVersion;  // if negative, then the atom does not contain version and flags data.
    private int mFlags;

    // create an empty atom of the given type.
    public Atom(String type) {
        mSize = 8;
        mType = getTypeInt(type);
        mData = null;
        mChildren = null;
        mVersion = -1;
        mFlags = 0;
    }

    // create an empty atom of type type, with a given version and flags.
    public Atom(String type, byte version, int flags) {
        mSize = 12;
        mType = getTypeInt(type);
        mData = null;
        mChildren = null;
        mVersion = version;
        mFlags = flags;
    }

    // set the size field of the atom based on its content.
    private void setSize() {
        int size = 8;  // type + size
        if (mVersion >= 0) {
            size += 4; // version + flags
        }
        if (mData != null) {
            size += mData.length;
        } else if (mChildren != null) {
            for (Atom child : mChildren) {
                size += child.getSize();
            }
        }
        mSize = size;
    }

    // get the size of the this atom.
    public int getSize() {
        return mSize;
    }

    private int getTypeInt(String type_str) {
        int type = 0;
        type |= (byte)(type_str.charAt(0)) << 24;
        type |= (byte)(type_str.charAt(1)) << 16;
        type |= (byte)(type_str.charAt(2)) << 8;
        type |= (byte)(type_str.charAt(3));
        return type;
    }

    public int getTypeInt() {
        return mType;
    }

    public String getTypeStr() {
        String type = "";
        type += (char)((byte)((mType >> 24) & 0xFF));
        type += (char)((byte)((mType >> 16) & 0xFF));
        type += (char)((byte)((mType >> 8) & 0xFF));
        type += (char)((byte)(mType & 0xFF));
        return type;
    }

    public boolean setData(byte[] data) {
        if (mChildren != null || data == null) {
            // TODO(nfaralli): log something here
            return false;
        }
        mData = data;
        setSize();
        return true;
    }

    public byte[] getData() {
        return mData;
    }

    public boolean addChild(Atom child) {
        if (mData != null || child == null) {
            // TODO(nfaralli): log something here
            return false;
        }
        int numChildren = 1;
        if (mChildren != null) {
            numChildren += mChildren.length;
        }
        Atom[] children = new Atom[numChildren];
        if (mChildren != null) {
            System.arraycopy(mChildren, 0, children, 0, mChildren.length);
        }
        children[numChildren - 1] = child;
        mChildren = children;
        setSize();
        return true;
    }

    // return the child atom of the corresponding type.
    // type can contain grand children: e.g. type = "trak.mdia.minf"
    // return null if the atom does not contain such a child.
    public Atom getChild(String type) {
        if (mChildren == null) {
            return null;
        }
        String[] types = type.split("\\.", 2);
        for (Atom child : mChildren) {
            if (child.getTypeStr().equals(types[0])) {
                if (types.length == 1) {
                    return child;
                } else {
                    return child.getChild(types[1]);
                }
            }
        }
        return null;
    }

    // return a byte array containing the full content of the atom (including header)
    public byte[] getBytes() {
        byte[] atom_bytes = new byte[mSize];
        int offset = 0;

        atom_bytes[offset++] = (byte)((mSize >> 24) & 0xFF);
        atom_bytes[offset++] = (byte)((mSize >> 16) & 0xFF);
        atom_bytes[offset++] = (byte)((mSize >> 8) & 0xFF);
        atom_bytes[offset++] = (byte)(mSize & 0xFF);
        atom_bytes[offset++] = (byte)((mType >> 24) & 0xFF);
        atom_bytes[offset++] = (byte)((mType >> 16) & 0xFF);
        atom_bytes[offset++] = (byte)((mType >> 8) & 0xFF);
        atom_bytes[offset++] = (byte)(mType & 0xFF);
        if (mVersion >= 0) {
            atom_bytes[offset++] = mVersion;
            atom_bytes[offset++] = (byte)((mFlags >> 16) & 0xFF);
            atom_bytes[offset++] = (byte)((mFlags >> 8) & 0xFF);
            atom_bytes[offset++] = (byte)(mFlags & 0xFF);
        }
        if (mData != null) {
            System.arraycopy(mData, 0, atom_bytes, offset, mData.length);
        } else if (mChildren != null) {
            byte[] child_bytes;
            for (Atom child : mChildren) {
                child_bytes = child.getBytes();
                System.arraycopy(child_bytes, 0, atom_bytes, offset, child_bytes.length);
                offset += child_bytes.length;
            }
        }
        return atom_bytes;
    }

    // Used for debugging purpose only.
    public String toString() {
        StringBuilder str = new StringBuilder();
        byte[] atom_bytes = getBytes();

        for (int i = 0; i < atom_bytes.length; i++) {
            if(i % 8 == 0 && i > 0) {
                str.append('\n');
            }
            str.append(String.format("0x%02X", atom_bytes[i]));
            if (i < atom_bytes.length - 1) {
                str.append(',');
                if (i % 8 < 7) {
                    str.append(' ');
                }
            }
        }
        str.append('\n');
        return str.toString();
    }
}