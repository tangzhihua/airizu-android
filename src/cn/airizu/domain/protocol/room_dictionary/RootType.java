package cn.airizu.domain.protocol.room_dictionary;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class RootType implements Serializable {
	private String typeName;
	private String typeId;
	
	public RootType(String typeName, String typeId) {
		this.typeName = typeName;
		this.typeId = typeId;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public String getTypeId() {
		return typeId;
	}
	
	@Override
	public String toString() {
		return "RootType [typeName=" + typeName + ", typeId=" + typeId + "]";
	}
}
