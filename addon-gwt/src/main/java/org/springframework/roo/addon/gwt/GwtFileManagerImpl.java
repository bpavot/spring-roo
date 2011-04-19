package org.springframework.roo.addon.gwt;

import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.PhysicalTypeMetadataProvider;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.process.manager.FileManager;

/**
 * Implementation of {@link GwtFileManager}.
 *
 * @author James Tyrrell
 * @since 1.1.1
 */
@Component
@Service
public class GwtFileManagerImpl implements GwtFileManager {
	private static String ROO_EDIT_WARNING = "// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.\n\n";
	@Reference private FileManager fileManager;
	@Reference private PhysicalTypeMetadataProvider physicalTypeMetadataProvider;
	@Reference private TypeLocationService typeLocationService;

	public void write(String destFile, String newContents) {
		write(destFile, newContents, true);
	}

	private void write(String destFile, String newContents, boolean overwrite) {
		// Write to disk, or update a file if it is already present and overwriting is allowed
		if (!fileManager.exists(destFile) || overwrite) {
			fileManager.createOrUpdateTextFileIfRequired(destFile, newContents, true);
		}
	}

	public void write(ClassOrInterfaceTypeDetails typeDetails, boolean includeWarning) {
		String destFile = typeLocationService.getPhysicalLocationCanonicalPath(typeDetails.getDeclaredByMetadataId());
		includeWarning &= !destFile.endsWith(".xml");
		includeWarning |= destFile.endsWith("_Roo_Gwt.java");
		String fileContents = physicalTypeMetadataProvider.getCompilationUnitContents(typeDetails);
		if (includeWarning) {
			fileContents = ROO_EDIT_WARNING + fileContents;
		} else if (fileManager.exists(destFile)) {
			return;
		}
		write(destFile, fileContents, includeWarning);
	}

	public void write(List<ClassOrInterfaceTypeDetails> typeDetails, boolean includeWarning) {
		for (ClassOrInterfaceTypeDetails typeDetail : typeDetails) {
			write(typeDetail, includeWarning);
		}
	}
}
