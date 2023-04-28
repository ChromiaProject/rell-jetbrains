package net.postchain.rellide.jetbrains.colors
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import net.postchain.rellide.jetbrains.language.RellIcons
import net.postchain.rellide.jetbrains.language.RellLanguage
import net.postchain.rellide.jetbrains.language.RellSyntaxHighlighter

class RellColorSettingsPage : ColorSettingsPage {
    private val ATTRIBUTES: Array<AttributesDescriptor> = RellColor.values().map { it.attributesDescriptor }.toTypedArray()
    private val ANNOTATOR_TAGS = RellColor.values().associateBy({ it.name }, { it.textAttributesKey })

    private val DEMO_TEXT by lazy {
        """
            struct category_node {
            	name;
            	interfaces: list<text>;
            	subcategories: list<category_node>;
            }

            function _category_node(
            	name, 
            	interface_map: map<name, list<name>>, 
            	category_names: map<name, name>
            ): category_node {
            	val category_nodes = list<category_node>();
            	
            	if (not interface_map.contains(name)) {
            		return category_node(
            			name = if (category_names.contains(name)) category_names[name] else name,
            			interfaces = [name],
            			subcategories = list<category_node>()
            		);
            	}
            	
            	for (child in interface_map[name]) {
            		category_nodes.add(_category_node(child, interface_map, category_names));
            	}
            	
            	return category_node(
            		name = if (category_names.contains(name)) category_names[name] else name,
            		interfaces = [name],
            		subcategories = category_nodes
            	);
            }
        """.trimIndent()
    }

    override fun getDisplayName() = RellLanguage.INSTANCE.displayName
    override fun getIcon() = RellIcons.FILE
    override fun getAttributeDescriptors() = ATTRIBUTES
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getHighlighter() = RellSyntaxHighlighter
    override fun getAdditionalHighlightingTagToDescriptorMap() = ANNOTATOR_TAGS
    override fun getDemoText() = DEMO_TEXT
}