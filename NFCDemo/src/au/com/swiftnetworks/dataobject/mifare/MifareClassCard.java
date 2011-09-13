//------------------------------------------------------------------------------
//                         COPYRIGHT 2011 SWIFTNETWORKS
//                           ALL RIGHTS RESERVED.
//                     GUIDEBEE CONFIDENTIAL PROPRIETARY
///////////////////////////////////// REVISIONS ////////////////////////////////
// Date       Name                 Tracking #         Description
// ---------  -------------------  ----------         --------------------------
// 13SEP2011  James Shen                 	          Initial Creation
////////////////////////////////////////////////////////////////////////////////
//--------------------------------- PACKAGE ------------------------------------
package au.com.swiftnetworks.dataobject.mifare;

//--------------------------------- IMPORTS ------------------------------------
import android.util.Log;
import au.com.swiftnetworks.util.Converter;

//[------------------------------ MAIN CLASS ----------------------------------]
//--------------------------------- REVISIONS ----------------------------------
//Date       Name                 Tracking #         Description
//--------   -------------------  -------------      --------------------------
//13SEP2011  James Shen                 	         Initial Creation
////////////////////////////////////////////////////////////////////////////////
/**
* This class stands for mifare class card.
* <hr>
* <b>&copy; Copyright 2011 Swiftnetworks, Inc. All Rights Reserved.</b>
* 
* @version 1.00, 13/09/11
* @author Swiftnetworks Pty Ltd.
*/
public class MifareClassCard {
	
	/**
	 * the size of the sector.
	 */
	private int SECTORCOUNT=16;
	
	private final static String TAG="MifareCardInfo";
	
	/**
	 * sectors.
	 */
	private final MifareSector[] sectors;
	
	////////////////////////////////////////////////////////////////////////////
	//--------------------------------- REVISIONS ------------------------------
	// Date       Name                 Tracking #         Description
	// ---------  -------------------  -------------      ----------------------
	// 13SEP2011  James Shen                 	          Initial Creation
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor.
	 * @param sectorSize size of the sectors.
	 */
	public MifareClassCard(int sectorSize){
		sectors=new MifareSector[sectorSize];
		SECTORCOUNT=sectorSize;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//--------------------------------- REVISIONS ------------------------------
	// Date       Name                 Tracking #         Description
	// ---------  -------------------  -------------      ----------------------
	// 13SEP2011  James Shen                 	          Initial Creation
	////////////////////////////////////////////////////////////////////////////
	/**
	 * get sector at given index.
	 * @param index the index of the sector.
	 * @return the sector at given index;
	 */
	public MifareSector getSector(int index){
		if(index>=SECTORCOUNT){
			throw new IllegalArgumentException("Invaid index for sector"); 
		}
		return sectors[index];
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	//--------------------------------- REVISIONS ------------------------------
	// Date       Name                 Tracking #         Description
	// ---------  -------------------  -------------      ----------------------
	// 13SEP2011  James Shen                 	          Initial Creation
	////////////////////////////////////////////////////////////////////////////
	/**
	* set sector at given index.
	* @param index the index of the sector.
	*/
	public void setSector(int index, MifareSector sector) {
		if (index >= SECTORCOUNT) {
			throw new IllegalArgumentException("Invaid index for sector");
		}
		sectors[index]=sector;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//--------------------------------- REVISIONS ------------------------------
	// Date       Name                 Tracking #         Description
	// ---------  -------------------  -------------      ----------------------
	// 13SEP2011  James Shen                 	          Initial Creation
	////////////////////////////////////////////////////////////////////////////
	/**
	 * get the count of the sector.
	 * @return the count of the sector.
	 */
	public int getSectorCount(){
		return SECTORCOUNT;
		
	}

	////////////////////////////////////////////////////////////////////////////
	//--------------------------------- REVISIONS ------------------------------
	// Date       Name                 Tracking #         Description
	// ---------  -------------------  -------------      ----------------------
	// 13SEP2011  James Shen                 	          Initial Creation
	////////////////////////////////////////////////////////////////////////////
	/**
	 * debug print information.
	 */
	public void debugPrint() {
		for (int i = 0; i < SECTORCOUNT; i++) {
			MifareSector sector = sectors[i];
			if (sector != null) {
				Log.i(TAG, "Sector " + i);
				for (int j = 0; j < MifareSector.BLOCKCOUNT; j++) {
					MifareBlock block = sector.blocks[j];
					byte[] raw = block.getData();
					String hexString = Converter.getHexString(raw, raw.length);
					Log.i(TAG, hexString);

				}
			}
		}
	}
}
