package cn.airizu.domain.net_entitydata_tools;

/**
 * 将网络返回的数据, 解压成可识别的字符串(在这里完成数据的解密)
 * @author zhihua.tang
 *
 */
public interface INetRespondRawEntityDataUnpack {
	public String unpackNetRespondRawEntityData(byte[] rawData) throws Exception;
}
