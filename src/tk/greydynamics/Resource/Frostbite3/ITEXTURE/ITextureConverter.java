package tk.greydynamics.Resource.Frostbite3.ITEXTURE;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.DDS.DDS_HEADER;
import tk.greydynamics.Resource.DDS.DDS_PIXELFORMAT;
import tk.greydynamics.Resource.Frostbite3.Cas.CasManager;

public class ITextureConverter {
	
	public static DDS_HEADER getDDSHeader(ITexture itexture){
		DDS_HEADER header = new DDS_HEADER();
		header.setDwSize(124);
		header.setDwFlags(659471);
		header.setDwHeight(itexture.getHeight());
		header.setDwWidth(itexture.getWidth());
		header.setDwPitchOrLinearSize(itexture.getMipSizes()[0]);
		header.setDwDepth(itexture.getDepth());
		header.setDwMipMapCount(itexture.getNumSizes());
		header.setDwReserved1(new int[11]);
		for (int i=0; i<header.getDwReserved1().length;i++){
			header.getDwReserved1()[i] = 0;
		}
		header.setDwCaps(4096);
		header.setDwCaps2(0);
		
		/*PixelFormat*/
		DDS_PIXELFORMAT pixelformat = new DDS_PIXELFORMAT(
				/*dwSize*/ 32,
				/*dwFlags*/ 4,
				/*dwFourCC*/ 827611204,
				/*dwRGBBitCount*/ 0,
				/*dwRBitMask*/ 0,
				/*dwGBitMask*/ 0,
				/*dwBBitMask*/ 0,
				/*dwABitMask)*/ 0
		);
		
		if (itexture.getTextureType()==ITexture.TT_Cube){
			header.setDwCaps2(65024);
			header.setDwCaps(header.getDwCaps() | 8);
		}
		
		if (!ITexture.PixelFormatTypes.containsKey(itexture.getPixelFormat())){
			
			switch (itexture.getPixelFormat()){
				case ITexture.TF_ABGR32F:
					pixelformat.setDwFourCC(0);
					pixelformat.setDwRGBBitCount((itexture.getPixelFormat() == ITexture.TF_ABGR32F) ? 128 : 32);
					pixelformat.setDwGBitMask(16711680);
					pixelformat.setDwGBitMask(65280);
					pixelformat.setDwBBitMask(-16777216);
					pixelformat.setDwFlags(65);
					
					header.setDwFlags(-524289);
					header.setDwFlags(header.getDwFlags() | 8);
					break;
				case ITexture.TF_L8:
					pixelformat.setDwFourCC(0);
					pixelformat.setDwRGBBitCount(8);
					pixelformat.setDwABitMask(255);
					pixelformat.setDwFlags(2);
					
					header.setDwFlags(-524289);
					header.setDwFlags(header.getDwFlags() | 8);
					break;
				case ITexture.TF_L16:
					pixelformat.setDwFourCC(0);
					pixelformat.setDwRGBBitCount(16);
					pixelformat.setDwRBitMask(65535);
					pixelformat.setDwFlags(131072);
					
					header.setDwFlags(-524289);
					header.setDwFlags(header.getDwFlags() | 8);
					break;
				case ITexture.TF_ARGB8888:
					//Loading Screens
					pixelformat.setDwFourCC(0);
					pixelformat.setDwABitMask(0xff000000);
					pixelformat.setDwBBitMask(0xff);
					pixelformat.setDwGBitMask(0xff00);
					pixelformat.setDwRBitMask(0xff0000);
					pixelformat.setDwRGBBitCount(32);
					pixelformat.setDwFlags(0x41);
					
					header.setDwFlags(0x81007);					
					break;
				case ITexture.TF_ABGR16:
					break;
				case ITexture.TF_ABGR16F:
					break;
			}
		}else{
			pixelformat.setDwFourCC(ITexture.PixelFormatTypes.get(itexture.getPixelFormat()));
			if (itexture.getPixelFormat() == ITexture.TF_DXT1A){
				pixelformat.setDwFlags(pixelformat.getDwFlags() | 1);
			}
		}
		header.setPixelformat(pixelformat);
		
		
		return header;
	}
	
	public static ITexture getITextureHeader(byte[] ddsFile, ITexture originalITextureHeader, String newGUID){
		DDS_HEADER ddsHeader = new DDS_HEADER(ddsFile, null);
		ITexture itexture = new ITexture();
		
		//itexture.setFirstMip(originalITextureHeader.getFirstMip());
		//We don't want to calculate range args so, we start at the first mip.
		itexture.setFirstMip((byte) 0x1);
		//itexture.setFirstMip(originalITextureHeader.getFirstMip());
		
		itexture.setUnknown(originalITextureHeader.getUnknown());
		
		if (originalITextureHeader.getTextureType() == ITexture.TF_NormalDXT1 
				&& ddsHeader.getPixelformat().getDwFourCC() != ITexture.DDS_DXT1)
		{
			System.err.println("DDS does NOT MATCH DXT1 normalmap.\n"
							 + "Make sure to use the original Format.\n"
							 + "--Canceling process!--");
			return null;
		}
		switch (ddsHeader.getPixelformat().getDwFourCC()){
			case 0:
				switch (ddsHeader.getPixelformat().getDwRGBBitCount()){
					case 8:
						itexture.setPixelFormat(ITexture.TF_L8);
						break;
					case 16:
						itexture.setPixelFormat(ITexture.TF_L16);
						break;
					case 128:
						itexture.setPixelFormat(ITexture.TF_ABGR32F);
						break;
					case 32:
						itexture.setPixelFormat(ITexture.TF_ARGB8888);
						break;
					default:
						System.err.println("The RGBBitCount of " + ddsHeader.getPixelformat().getDwRGBBitCount() + " is not assigned to any valid formats.\n"
								+ "You could try to use a DXT1(no alpha) or DXT5(Alpha) Texture instead.\n"
								+ "Please submit your texture and format details to @CaptainSpleXx on GitHub!");
						return null;
				}
				break;
			case ITexture.DDS_ABGR32F:
				itexture.setPixelFormat(ITexture.TF_ABGR32F);
				break;
			case ITexture.DDS_DXT1:
				itexture.setPixelFormat(ITexture.TF_DXT1);
				break;
			case ITexture.DDS_NormalDXN:
				itexture.setPixelFormat(ITexture.TF_NormalDXN);
				break;
			case ITexture.DDS_DXT5:
				itexture.setPixelFormat(ITexture.TF_DXT5);
				break;
			default:
				itexture.setPixelFormat(ITexture.TF_Unknown);
				System.err.println("DDS File has an unknown format. Make sure to use a supported one!");
				return null;
		}
				
		itexture.setTextureType(((ddsHeader.getDwCaps2() == 65024) ? ITexture.TT_Cube : ITexture.TT_2d));
		
		if (originalITextureHeader.getTextureType() == ITexture.TT_Cube && ddsHeader.getDwCaps2() != 65024)
		{
			System.err.println("Original texture type is a cubemap! - Can't convert to DDS!");
			return null;
		}
		
		
		if (itexture.getTextureType()!=originalITextureHeader.getTextureType()||itexture.getPixelFormat()!=originalITextureHeader.getPixelFormat()){
			System.err.println("Can't continue. New file has a diffrent TextureType or PixelFormat!");
			return null;
		}
		
		itexture.setNameHash(originalITextureHeader.getNameHash());
		itexture.setWidth((short) ddsHeader.getDwWidth());
		itexture.setHeight((short) ddsHeader.getDwHeight());
		itexture.setDepth((byte) 0x1);
		itexture.setSliceCount((short) 1);
		itexture.setNumSizes((byte) ddsHeader.getDwMipMapCount());
		
		itexture.setChunkSize(ddsFile.length-128/*Substract Header with FourCC*/);
		itexture.setName(originalITextureHeader.getName());
		
		
		itexture.setMipSizes(new int[15]);
		int width = ddsHeader.getDwWidth();
		int height = ddsHeader.getDwHeight();
		if (ddsHeader.getPixelformat().getDwFourCC() == 0)
		{
			int num3 = 0;
			switch (itexture.getPixelFormat())
				{
				case ITexture.TF_ABGR16F:
					for (int i = 0; i < ddsHeader.getDwMipMapCount(); i++)
					{
						/* Alpha Blue Green Red, each is using a single float.
						 * So we get a total of 16 Bytes á pixel.
						 */
						itexture.getMipSizes()[i] = width * height * 16;
						width >>= 1;
						height >>= 1;
					}
					break;
				default:
					System.err.println(itexture.getPixelFormat()+" is currently not supported!");
					break;
			}
		}
		else
		{
			/*
			 * DXT:
			 * When compressing a 4 colour palette is determined for these 16 pixels.
			 * Afterwards each pixel will get an index into this palette, which only requires 2 bit per pixel.
			 * For the palette only two colours are stored, the two extremes, and the other two colours are interpolated between these extremes.
			 * The colour information is also stored with a compression so that only 16 bits are used per colour.
			 * This means that these 16 pixels of the texture only take 64 bits to store (32 for the palette and 32 for the indexing).
			 * 
			 * Besides that the palette only stores two colour, let's call them colour1 and colour2.
			 * The other two colours that can be indexed are linearly interpolated.
			 * So that means that colour3 is 2/3 of colour1 plus 1/3 of colour2 and for colour4 the reverse is true (1/3 if colour1 plus 2/3 of colour2).
			 * This means that if the 4 most common colours used in your 16 pixels are not on a one linear line in the colour space, you will loose some colours.
			 * 
			 * 
			 * 
			 * DXT1 + Alpha:
			 * A 1 bit alpha channel means that the transparency is either on or off.
			 * To store this one of the colours of the colour palette will get the meaning completely transparent.
			 * The other three colours are left to store the colour information of your pixels.
			 * So for the two colours stored in this case only one has to be interpolated,
			 * colour3 is 1/2 colour1 plus 1/2 colour2.
			 * This means even less resolution is left for different shades of colours.
			 * 
			 * 
			 * 
			 * DXT5 + Alpha:
			 * For the alpha information it uses a palette, similar to the way the colour information is also stored.
			 * This palette contains a minimum and maximum alpha value.
			 * Then 6 other alpha values are interpolated between this minimum and maximum.
			 * This thus allows more gradual changes of the alpha value.
			 * A second variant does interpolate only 4 other alpha values between the minimum and maximum,
			 * but also adds an alpha value of 0 and 1 (for fully transparent and not transparent).
			 * For some textures this might give better results.
			 * 
			 * */
			int bits = 8;
			int pixelFormat = itexture.getPixelFormat();
			if (pixelFormat == ITexture.TF_DXT5 || pixelFormat == ITexture.TF_NormalDXN)
			{
				bits = 16;
			}
			for (int j = 0; j < ddsHeader.getDwMipMapCount(); j++)
			{
				int tWidth = width / 4;
				int tHeight = height / 4;
				/*num7 = ((num7 < 1) ? 1 : num7);
				num8 = ((num8 < 1) ? 1 : num8);*/
				itexture.getMipSizes()[j] = tWidth * bits * tHeight;
				width >>= 1;
				height >>= 1;
			}
		}
		itexture.setMipSizes(originalITextureHeader.getMipSizes());
		
		itexture.setMipOneEndOffset(itexture.getMipSizes()[0] + (CasManager.calculateNumberOfBlocks(itexture.getMipSizes()[0]) * CasManager.blockHeaderNumBytes));
		if (itexture.getNumSizes()>=2){
			itexture.setMipTwoEndOffset(itexture.getMipOneEndOffset() + itexture.getMipSizes()[1] + (CasManager.calculateNumberOfBlocks(itexture.getMipSizes()[1]) * CasManager.blockHeaderNumBytes));
		}
		
		itexture.setChunkID(FileHandler.hexStringToByteArray(newGUID));
		System.out.println("ITexture Header created for Chunk "+newGUID);
		
		return itexture;
	}
	
	
	public static byte[] getBlockData(byte[] ddsFileBytes){
		if (ddsFileBytes!=null){
			//DDS Header has a size of 0x80!
			byte[] blockData = new byte[ddsFileBytes.length-0x80];
			for (int i=0; i<blockData.length; i++){
				blockData[i] = ddsFileBytes[i+0x80];
			}			
			return blockData;
		}else{
			System.err.println("DDS File can not be null. (ITextureConverter.getBlockData)!");
			return null;
		}
	}

}
