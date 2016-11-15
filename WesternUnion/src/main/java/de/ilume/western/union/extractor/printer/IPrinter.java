package de.ilume.western.union.extractor.printer;

import java.io.Closeable;

import de.ilume.western.union.extractor.data.AddressData;
import de.ilume.western.union.extractor.data.PrincipalData;

/**
 * 
 * @author Benedikt Sixt
 *
 */
public interface IPrinter extends Closeable {
	public void print(final AddressData data, final PrincipalData pData);
}
