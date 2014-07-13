/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package de.jungblut.thrift;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.EncodingUtils;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;

public class Match implements org.apache.thrift.TBase<Match, Match._Fields>, java.io.Serializable, Cloneable, Comparable<Match> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Match");

  private static final org.apache.thrift.protocol.TField GAME_READY_FIELD_DESC = new org.apache.thrift.protocol.TField("gameReady", org.apache.thrift.protocol.TType.BOOL, (short)1);
  private static final org.apache.thrift.protocol.TField NUM_CURRENT_PLAYERS_FIELD_DESC = new org.apache.thrift.protocol.TField("numCurrentPlayers", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField SESSION_TOKEN_FIELD_DESC = new org.apache.thrift.protocol.TField("sessionToken", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField CLIENT_IDENTIFIER_FIELD_DESC = new org.apache.thrift.protocol.TField("clientIdentifier", org.apache.thrift.protocol.TType.STRING, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new MatchStandardSchemeFactory());
    schemes.put(TupleScheme.class, new MatchTupleSchemeFactory());
  }

  public boolean gameReady; // required
  public int numCurrentPlayers; // required
  public String sessionToken; // required
  public String clientIdentifier; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    GAME_READY((short)1, "gameReady"),
    NUM_CURRENT_PLAYERS((short)2, "numCurrentPlayers"),
    SESSION_TOKEN((short)3, "sessionToken"),
    CLIENT_IDENTIFIER((short)4, "clientIdentifier");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // GAME_READY
          return GAME_READY;
        case 2: // NUM_CURRENT_PLAYERS
          return NUM_CURRENT_PLAYERS;
        case 3: // SESSION_TOKEN
          return SESSION_TOKEN;
        case 4: // CLIENT_IDENTIFIER
          return CLIENT_IDENTIFIER;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __GAMEREADY_ISSET_ID = 0;
  private static final int __NUMCURRENTPLAYERS_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.GAME_READY, new org.apache.thrift.meta_data.FieldMetaData("gameReady", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.NUM_CURRENT_PLAYERS, new org.apache.thrift.meta_data.FieldMetaData("numCurrentPlayers", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.SESSION_TOKEN, new org.apache.thrift.meta_data.FieldMetaData("sessionToken", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CLIENT_IDENTIFIER, new org.apache.thrift.meta_data.FieldMetaData("clientIdentifier", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Match.class, metaDataMap);
  }

  public Match() {
  }

  public Match(
    boolean gameReady,
    int numCurrentPlayers,
    String sessionToken,
    String clientIdentifier)
  {
    this();
    this.gameReady = gameReady;
    setGameReadyIsSet(true);
    this.numCurrentPlayers = numCurrentPlayers;
    setNumCurrentPlayersIsSet(true);
    this.sessionToken = sessionToken;
    this.clientIdentifier = clientIdentifier;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Match(Match other) {
    __isset_bitfield = other.__isset_bitfield;
    this.gameReady = other.gameReady;
    this.numCurrentPlayers = other.numCurrentPlayers;
    if (other.isSetSessionToken()) {
      this.sessionToken = other.sessionToken;
    }
    if (other.isSetClientIdentifier()) {
      this.clientIdentifier = other.clientIdentifier;
    }
  }

  public Match deepCopy() {
    return new Match(this);
  }

  @Override
  public void clear() {
    setGameReadyIsSet(false);
    this.gameReady = false;
    setNumCurrentPlayersIsSet(false);
    this.numCurrentPlayers = 0;
    this.sessionToken = null;
    this.clientIdentifier = null;
  }

  public boolean isGameReady() {
    return this.gameReady;
  }

  public Match setGameReady(boolean gameReady) {
    this.gameReady = gameReady;
    setGameReadyIsSet(true);
    return this;
  }

  public void unsetGameReady() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __GAMEREADY_ISSET_ID);
  }

  /** Returns true if field gameReady is set (has been assigned a value) and false otherwise */
  public boolean isSetGameReady() {
    return EncodingUtils.testBit(__isset_bitfield, __GAMEREADY_ISSET_ID);
  }

  public void setGameReadyIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __GAMEREADY_ISSET_ID, value);
  }

  public int getNumCurrentPlayers() {
    return this.numCurrentPlayers;
  }

  public Match setNumCurrentPlayers(int numCurrentPlayers) {
    this.numCurrentPlayers = numCurrentPlayers;
    setNumCurrentPlayersIsSet(true);
    return this;
  }

  public void unsetNumCurrentPlayers() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __NUMCURRENTPLAYERS_ISSET_ID);
  }

  /** Returns true if field numCurrentPlayers is set (has been assigned a value) and false otherwise */
  public boolean isSetNumCurrentPlayers() {
    return EncodingUtils.testBit(__isset_bitfield, __NUMCURRENTPLAYERS_ISSET_ID);
  }

  public void setNumCurrentPlayersIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __NUMCURRENTPLAYERS_ISSET_ID, value);
  }

  public String getSessionToken() {
    return this.sessionToken;
  }

  public Match setSessionToken(String sessionToken) {
    this.sessionToken = sessionToken;
    return this;
  }

  public void unsetSessionToken() {
    this.sessionToken = null;
  }

  /** Returns true if field sessionToken is set (has been assigned a value) and false otherwise */
  public boolean isSetSessionToken() {
    return this.sessionToken != null;
  }

  public void setSessionTokenIsSet(boolean value) {
    if (!value) {
      this.sessionToken = null;
    }
  }

  public String getClientIdentifier() {
    return this.clientIdentifier;
  }

  public Match setClientIdentifier(String clientIdentifier) {
    this.clientIdentifier = clientIdentifier;
    return this;
  }

  public void unsetClientIdentifier() {
    this.clientIdentifier = null;
  }

  /** Returns true if field clientIdentifier is set (has been assigned a value) and false otherwise */
  public boolean isSetClientIdentifier() {
    return this.clientIdentifier != null;
  }

  public void setClientIdentifierIsSet(boolean value) {
    if (!value) {
      this.clientIdentifier = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case GAME_READY:
      if (value == null) {
        unsetGameReady();
      } else {
        setGameReady((Boolean)value);
      }
      break;

    case NUM_CURRENT_PLAYERS:
      if (value == null) {
        unsetNumCurrentPlayers();
      } else {
        setNumCurrentPlayers((Integer)value);
      }
      break;

    case SESSION_TOKEN:
      if (value == null) {
        unsetSessionToken();
      } else {
        setSessionToken((String)value);
      }
      break;

    case CLIENT_IDENTIFIER:
      if (value == null) {
        unsetClientIdentifier();
      } else {
        setClientIdentifier((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case GAME_READY:
      return Boolean.valueOf(isGameReady());

    case NUM_CURRENT_PLAYERS:
      return Integer.valueOf(getNumCurrentPlayers());

    case SESSION_TOKEN:
      return getSessionToken();

    case CLIENT_IDENTIFIER:
      return getClientIdentifier();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case GAME_READY:
      return isSetGameReady();
    case NUM_CURRENT_PLAYERS:
      return isSetNumCurrentPlayers();
    case SESSION_TOKEN:
      return isSetSessionToken();
    case CLIENT_IDENTIFIER:
      return isSetClientIdentifier();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Match)
      return this.equals((Match)that);
    return false;
  }

  public boolean equals(Match that) {
    if (that == null)
      return false;

    boolean this_present_gameReady = true;
    boolean that_present_gameReady = true;
    if (this_present_gameReady || that_present_gameReady) {
      if (!(this_present_gameReady && that_present_gameReady))
        return false;
      if (this.gameReady != that.gameReady)
        return false;
    }

    boolean this_present_numCurrentPlayers = true;
    boolean that_present_numCurrentPlayers = true;
    if (this_present_numCurrentPlayers || that_present_numCurrentPlayers) {
      if (!(this_present_numCurrentPlayers && that_present_numCurrentPlayers))
        return false;
      if (this.numCurrentPlayers != that.numCurrentPlayers)
        return false;
    }

    boolean this_present_sessionToken = true && this.isSetSessionToken();
    boolean that_present_sessionToken = true && that.isSetSessionToken();
    if (this_present_sessionToken || that_present_sessionToken) {
      if (!(this_present_sessionToken && that_present_sessionToken))
        return false;
      if (!this.sessionToken.equals(that.sessionToken))
        return false;
    }

    boolean this_present_clientIdentifier = true && this.isSetClientIdentifier();
    boolean that_present_clientIdentifier = true && that.isSetClientIdentifier();
    if (this_present_clientIdentifier || that_present_clientIdentifier) {
      if (!(this_present_clientIdentifier && that_present_clientIdentifier))
        return false;
      if (!this.clientIdentifier.equals(that.clientIdentifier))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(Match other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetGameReady()).compareTo(other.isSetGameReady());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetGameReady()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.gameReady, other.gameReady);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetNumCurrentPlayers()).compareTo(other.isSetNumCurrentPlayers());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNumCurrentPlayers()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.numCurrentPlayers, other.numCurrentPlayers);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSessionToken()).compareTo(other.isSetSessionToken());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSessionToken()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.sessionToken, other.sessionToken);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetClientIdentifier()).compareTo(other.isSetClientIdentifier());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetClientIdentifier()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.clientIdentifier, other.clientIdentifier);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Match(");
    boolean first = true;

    sb.append("gameReady:");
    sb.append(this.gameReady);
    first = false;
    if (!first) sb.append(", ");
    sb.append("numCurrentPlayers:");
    sb.append(this.numCurrentPlayers);
    first = false;
    if (!first) sb.append(", ");
    sb.append("sessionToken:");
    if (this.sessionToken == null) {
      sb.append("null");
    } else {
      sb.append(this.sessionToken);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("clientIdentifier:");
    if (this.clientIdentifier == null) {
      sb.append("null");
    } else {
      sb.append(this.clientIdentifier);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class MatchStandardSchemeFactory implements SchemeFactory {
    public MatchStandardScheme getScheme() {
      return new MatchStandardScheme();
    }
  }

  private static class MatchStandardScheme extends StandardScheme<Match> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Match struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // GAME_READY
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.gameReady = iprot.readBool();
              struct.setGameReadyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // NUM_CURRENT_PLAYERS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.numCurrentPlayers = iprot.readI32();
              struct.setNumCurrentPlayersIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SESSION_TOKEN
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.sessionToken = iprot.readString();
              struct.setSessionTokenIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // CLIENT_IDENTIFIER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.clientIdentifier = iprot.readString();
              struct.setClientIdentifierIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Match struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(GAME_READY_FIELD_DESC);
      oprot.writeBool(struct.gameReady);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(NUM_CURRENT_PLAYERS_FIELD_DESC);
      oprot.writeI32(struct.numCurrentPlayers);
      oprot.writeFieldEnd();
      if (struct.sessionToken != null) {
        oprot.writeFieldBegin(SESSION_TOKEN_FIELD_DESC);
        oprot.writeString(struct.sessionToken);
        oprot.writeFieldEnd();
      }
      if (struct.clientIdentifier != null) {
        oprot.writeFieldBegin(CLIENT_IDENTIFIER_FIELD_DESC);
        oprot.writeString(struct.clientIdentifier);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MatchTupleSchemeFactory implements SchemeFactory {
    public MatchTupleScheme getScheme() {
      return new MatchTupleScheme();
    }
  }

  private static class MatchTupleScheme extends TupleScheme<Match> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Match struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetGameReady()) {
        optionals.set(0);
      }
      if (struct.isSetNumCurrentPlayers()) {
        optionals.set(1);
      }
      if (struct.isSetSessionToken()) {
        optionals.set(2);
      }
      if (struct.isSetClientIdentifier()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetGameReady()) {
        oprot.writeBool(struct.gameReady);
      }
      if (struct.isSetNumCurrentPlayers()) {
        oprot.writeI32(struct.numCurrentPlayers);
      }
      if (struct.isSetSessionToken()) {
        oprot.writeString(struct.sessionToken);
      }
      if (struct.isSetClientIdentifier()) {
        oprot.writeString(struct.clientIdentifier);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Match struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.gameReady = iprot.readBool();
        struct.setGameReadyIsSet(true);
      }
      if (incoming.get(1)) {
        struct.numCurrentPlayers = iprot.readI32();
        struct.setNumCurrentPlayersIsSet(true);
      }
      if (incoming.get(2)) {
        struct.sessionToken = iprot.readString();
        struct.setSessionTokenIsSet(true);
      }
      if (incoming.get(3)) {
        struct.clientIdentifier = iprot.readString();
        struct.setClientIdentifierIsSet(true);
      }
    }
  }

}

