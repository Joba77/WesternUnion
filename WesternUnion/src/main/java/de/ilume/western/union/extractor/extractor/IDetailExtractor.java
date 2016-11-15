package de.ilume.western.union.extractor.extractor;

import de.ilume.western.union.extractor.data.AddressData;
import de.ilume.western.union.extractor.data.PrincipalData;

/**
 * 
 * @author Benedikt Sixt
 *
 */
public interface IDetailExtractor {
	public void detailsFor(final AddressData data, final PrincipalData pData);
}
