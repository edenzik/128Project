//package com.wrangler.ui.export;
//
//import com.vaadin.server.FileDownloader;
//import com.vaadin.server.Resource;
//import com.vaadin.server.StreamResource;
//import com.vaadin.ui.UI;
//
//public class ExportStream extends FileDownloader {
//
//	public ExportStream(Resource resource) {
//		super(resource);
//		// TODO Auto-generated constructor stub
//	}
//
//	public ExportStream(){
//		super(null);
//		createResource();
//	}
//
//	private StreamResource createResource() {
//		return new StreamResource(new StreamSource() {
//
//			@Override
//			public InputStream getStream() {
//				String text = "My image";
//
//				BufferedImage bi = new BufferedImage(100, 30, BufferedImage.TYPE_3BYTE_BGR);
//				bi.getGraphics().drawChars(text.toCharArray(), 0, text.length(), 10, 20);
//
//				try {
//					ByteArrayOutputStream bos = new ByteArrayOutputStream();
//					ImageIO.write(bi, "png", bos);
//					return new ByteArrayInputStream(bos.toByteArray());
//				} catch (IOException e) {
//					e.printStackTrace();
//					return null;
//				}
//
//			}
//		}, "myImage.png");
//	}
//
//}
