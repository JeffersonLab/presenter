var Aloha = window.Aloha || (window.Aloha = {});

Aloha.settings = {
    locale: 'en',
    toolbar: {
        tabs: [
            {
                label: 'Format',
                components: [
                    ['bold', 'italic'],
                    ['formatLink'],
                    ['toggleFormatlessPaste'],
                    ['orderedListFormatSelector', 'unorderedListFormatSelector'],
                    ['formatBlock']
                ]
            }
        ],
        exclude: ['tab.format.label','tab.insert.label','indentList', 'outdentList', 'insertLink', 'formatLink', 'strong', 'emphasis', 'underline', 'definitionListFormatSelector', 'subscript', 'superscript', 'strikethrough', 'code', 'quote']
    },
    plugins: {
        formatlesspaste: {
            config: {
                button: true, // if set to false the button will be hidden
                formatlessPasteOption: false, // default state of the button
                strippedElements: [// elements to be stripped from the pasted code
                    "a",
                    "em",
                    "strong",
                    "small",
                    "s",
                    "cite",
                    "q",
                    "dfn",
                    "abbr",
                    "time",
                    "code",
                    "var",
                    "samp",
                    "kbd",
                    "sub",
                    "sup",
                    "i",
                    "b",
                    "u",
                    "mark",
                    "ruby",
                    "rt",
                    "rp",
                    "bdi",
                    "bdo",
                    "ins",
                    "del",
                    "pre",
                    "li",
                    "ul",
                    "ol",
                    "h1",
                    "h2",
                    "h3",
                    "h4",
                    "h5",
                    "h6",
                    "img",
                    "strike",
                    "p",
                    "br",
                    "hr",
                    "blockquote",
                    "caption",
                    "col",
                    "colgroup",
                    "dd",
                    "dt",
                    "dl",
                    "div",
                    "object",
                    "span",
                    "table",
                    "tbody",
                    "thead",
                    "tfoot",
                    "th",
                    "td",
                    "tr"
                ]
            }
        },
        format: {
            config: ['b', 'i', 'p', 'h1', 'h2', 'h3'],
            editables: {
                // no formatting allowed for title
                '.title': []
            }
        },
        list: {
            config: ['ol', 'ul'],
            editables: {
                // No lists in the title.
                '.title': []
            }
        },
        link: {
            target: '_blank',
            objectTypeFilter: [""]
        }
    },
    smartContentChange: {
        delimiters: [':', ';', '.', '!', '?', ',', unescape('%u0009'), unescape('%u0020'), 'Enter'],
        idle: 1000,
        delay: 500
    },
    sidebar: {
        disabled: true
    },
    contentHandler: {
        insertHtml: ['word', 'generic', 'oembed', 'sanitize', 'formatless'],
        initEditable: ['sanitize'],
        getContents: ['blockelement', 'sanitize', 'basic'],
        sanitize: 'relaxed',
        allows: {
            elements: [
                'a', 'abbr', 'b', 'blockquote', 'br', 'caption', 'cite', 'code', 'col',
                'colgroup', 'dd', 'del', 'dl', 'dt', 'em', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
                'i', 'img', 'li', 'ol', 'p', 'pre', 'q', 'small', 'strike', 'strong',
                'sub', 'sup', 'table', 'tbody', 'td', 'tfoot', 'th', 'thead', 'tr', 'u',
                'ul', 'span', 'hr', 'object', 'div'
            ],
            attributes: {
                'a': ['href', 'title', 'id', 'class', 'target', 'data-gentics-aloha-repository', 'data-gentics-aloha-object-id'],
                'div': ['id', 'class'],
                'abbr': ['title'],
                'blockquote': ['cite'],
                'br': ['class'],
                'col': ['span', 'width'],
                'colgroup': ['span', 'width'],
                'img': ['align', 'alt', 'height', 'src', 'title', 'width', 'class'],
                'ol': ['start', 'type'],
                'q': ['cite'],
                'p': ['class'],
                'table': ['summary', 'width'],
                'td': ['abbr', 'axis', 'colspan', 'rowspan', 'width'],
                'th': ['abbr', 'axis', 'colspan', 'rowspan', 'scope', 'width'],
                'ul': ['type'],
                'span': ['class', 'style', 'lang', 'xml:lang']
            },
            protocols: {
                'a': {
                    'href': ['ftp', 'http', 'https', 'mailto', '__relative__']
                },
                'blockquote': {
                    'cite': ['http', 'https', '__relative__']
                },
                'img': {
                    'src': ['http', 'https', '__relative__']
                },
                'q': {
                    'cite': ['http', 'https', '__relative__']
                }
            }
        }
    }
};

