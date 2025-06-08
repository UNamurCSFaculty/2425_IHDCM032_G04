import { useEditor, EditorContent, BubbleMenu, Editor } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import ImageExtension from '@tiptap/extension-image'
import LinkExtension from '@tiptap/extension-link'
import PlaceholderExtension from '@tiptap/extension-placeholder'
import TextAlignExtension from '@tiptap/extension-text-align'
import YoutubeExtension from '@tiptap/extension-youtube'
import Heading from '@tiptap/extension-heading'
import { mergeAttributes } from '@tiptap/core'

import {
  Bold,
  Italic,
  Strikethrough,
  List,
  ListOrdered,
  Heading1,
  Heading2,
  Heading3,
  Quote,
  Undo,
  Redo,
  Image as ImageIcon,
  Link as LinkIcon,
  Youtube as YoutubeIcon,
  AlignLeft,
  AlignCenter,
  AlignRight,
  AlignJustify,
  Unlink,
} from 'lucide-react'
import React, { useEffect, useCallback, useRef, useState } from 'react'
import { useFieldContext } from '.'
import { Label } from '../ui/label'
import { Button } from '../ui/button'
import { FieldErrors } from './field-errors'
import { cn } from '@/lib/utils'
import SimpleTooltip from '../SimpleTooltip'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
  DialogClose,
} from '../ui/dialog'
import { Input } from '../ui/input'

type RichTextEditorFieldProps = {
  label: string
  required?: boolean
  tooltip?: string
  className?: string
  editorClassName?: string
  toolbarClassName?: string
  placeholder?: string
}

/**
 * Composant de barre d'outils pour l'éditeur Tiptap.
 * Fournit des boutons pour les actions courantes comme l'annulation, la mise en forme, les titres, les listes, l'alignement, les médias et les liens.
 */
const TiptapToolbar = ({
  editor,
  className,
  onOpenLinkDialog, // Nouvelle prop pour ouvrir le dialogue de lien
  onOpenYoutubeDialog, // Nouvelle prop pour ouvrir le dialogue YouTube
}: {
  editor: Editor | null
  className?: string
  onOpenLinkDialog: () => void
  onOpenYoutubeDialog: () => void
}) => {
  const imageInputRef = useRef<HTMLInputElement>(null)

  const handleImageUpload = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      if (!editor) return
      const file = event.target.files?.[0]
      if (file) {
        const reader = new FileReader()
        reader.onloadend = () => {
          editor
            .chain()
            .focus()
            .setImage({ src: reader.result as string })
            .run()
        }
        reader.readAsDataURL(file)
      }
    },
    [editor]
  )

  if (!editor) {
    return null
  }

  const toolbarGroups = [
    {
      label: 'History',
      items: [
        {
          action: () => editor.chain().focus().undo().run(),
          icon: <Undo className="h-4 w-4" />,
          disabled: !editor.can().chain().focus().undo().run(),
          label: 'Undo',
        },
        {
          action: () => editor.chain().focus().redo().run(),
          icon: <Redo className="h-4 w-4" />,
          disabled: !editor.can().chain().focus().redo().run(),
          label: 'Redo',
        },
      ],
    },
    {
      label: 'Formatting',
      items: [
        {
          action: () => editor.chain().focus().toggleBold().run(),
          isActive: editor.isActive('bold'),
          icon: <Bold className="h-4 w-4" />,
          disabled: !editor.can().chain().focus().toggleBold().run(),
          label: 'Bold',
        },
        {
          action: () => editor.chain().focus().toggleItalic().run(),
          isActive: editor.isActive('italic'),
          icon: <Italic className="h-4 w-4" />,
          disabled: !editor.can().chain().focus().toggleItalic().run(),
          label: 'Italic',
        },
        {
          action: () => editor.chain().focus().toggleStrike().run(),
          isActive: editor.isActive('strike'),
          icon: <Strikethrough className="h-4 w-4" />,
          disabled: !editor.can().chain().focus().toggleStrike().run(),
          label: 'Strikethrough',
        },
      ],
    },
    {
      label: 'Headings',
      items: [
        {
          action: () =>
            editor.chain().focus().toggleHeading({ level: 1 }).run(),
          isActive: editor.isActive('heading', { level: 1 }),
          icon: <Heading1 className="h-4 w-4" />,
          label: 'Heading 1',
        },
        {
          action: () =>
            editor.chain().focus().toggleHeading({ level: 2 }).run(),
          isActive: editor.isActive('heading', { level: 2 }),
          icon: <Heading2 className="h-4 w-4" />,
          label: 'Heading 2',
        },
        {
          action: () =>
            editor.chain().focus().toggleHeading({ level: 3 }).run(),
          isActive: editor.isActive('heading', { level: 3 }),
          icon: <Heading3 className="h-4 w-4" />,
          label: 'Heading 3',
        },
      ],
    },
    {
      label: 'Lists & Quote',
      items: [
        {
          action: () => editor.chain().focus().toggleBulletList().run(),
          isActive: editor.isActive('bulletList'),
          icon: <List className="h-4 w-4" />,
          label: 'Bullet List',
        },
        {
          action: () => editor.chain().focus().toggleOrderedList().run(),
          isActive: editor.isActive('orderedList'),
          icon: <ListOrdered className="h-4 w-4" />,
          label: 'Ordered List',
        },
        {
          action: () => editor.chain().focus().toggleBlockquote().run(),
          isActive: editor.isActive('blockquote'),
          icon: <Quote className="h-4 w-4" />,
          label: 'Blockquote',
        },
      ],
    },
    {
      label: 'Alignment',
      items: [
        {
          action: () => editor.chain().focus().setTextAlign('left').run(),
          isActive: editor.isActive({ textAlign: 'left' }),
          icon: <AlignLeft className="h-4 w-4" />,
          label: 'Align Left',
        },
        {
          action: () => editor.chain().focus().setTextAlign('center').run(),
          isActive: editor.isActive({ textAlign: 'center' }),
          icon: <AlignCenter className="h-4 w-4" />,
          label: 'Align Center',
        },
        {
          action: () => editor.chain().focus().setTextAlign('right').run(),
          isActive: editor.isActive({ textAlign: 'right' }),
          icon: <AlignRight className="h-4 w-4" />,
          label: 'Align Right',
        },
        {
          action: () => editor.chain().focus().setTextAlign('justify').run(),
          isActive: editor.isActive({ textAlign: 'justify' }),
          icon: <AlignJustify className="h-4 w-4" />,
          label: 'Align Justify',
        },
      ],
    },
    {
      label: 'Media & Links',
      items: [
        {
          action: () => imageInputRef.current?.click(),
          icon: <ImageIcon className="h-4 w-4" />,
          label: 'Insert Image',
        },
        {
          action: onOpenYoutubeDialog, // Utiliser la prop
          icon: <YoutubeIcon className="h-4 w-4" />,
          label: 'Insert YouTube Video',
        },
        {
          action: onOpenLinkDialog, // Utiliser la prop
          isActive: editor.isActive('link'),
          icon: <LinkIcon className="h-4 w-4" />,
          label: 'Set Link',
        },
        {
          action: () => editor.chain().focus().unsetLink().run(),
          disabled: !editor.isActive('link'),
          icon: <Unlink className="h-4 w-4" />,
          label: 'Unset Link',
        },
      ],
    },
  ]

  return (
    <>
      <div
        className={cn(
          'border-input mb-2 flex flex-wrap items-center gap-1 rounded-md border bg-transparent p-1',
          className
        )}
      >
        <input
          type="file"
          accept="image/*"
          ref={imageInputRef}
          onChange={handleImageUpload}
          className="hidden"
        />
        {toolbarGroups.map((group, groupIndex) => (
          <React.Fragment key={group.label}>
            {group.items.map(item => (
              <Button
                key={item.label}
                type="button"
                variant={item.isActive ? 'default' : 'outline'}
                size="icon"
                onClick={item.action}
                disabled={item.disabled}
                title={item.label}
                className="h-8 w-8"
              >
                {item.icon}
              </Button>
            ))}
            {groupIndex < toolbarGroups.length - 1 && (
              <div className="bg-border mx-1 h-6 w-px" />
            )}
          </React.Fragment>
        ))}
      </div>
    </>
  )
}

export const RichTextEditorField = ({
  label,
  required = false,
  tooltip,
  className,
  editorClassName,
  toolbarClassName,
  placeholder,
}: RichTextEditorFieldProps) => {
  const field = useFieldContext<string>()

  // États pour les dialogues déplacés ici
  const [isLinkDialogOpen, setIsLinkDialogOpen] = useState(false)
  const [linkUrl, setLinkUrl] = useState('')
  const [isYoutubeDialogOpen, setIsYoutubeDialogOpen] = useState(false)
  const [youtubeUrl, setYoutubeUrl] = useState('')

  const editor = useEditor({
    extensions: [
      StarterKit.configure({
        heading: false,
      }),
      Heading.configure({ levels: [1, 2, 3] }).extend({
        renderHTML({ node, HTMLAttributes: incomingHTMLAttributes }) {
          const level = node.attrs.level as 1 | 2 | 3
          const perLevelClasses: Record<typeof level, string> = {
            1: 'text-3xl font-bold my-4',
            2: 'text-2xl font-semibold my-3',
            3: 'text-xl font-medium my-2',
          }
          return [
            `h${level}`,
            mergeAttributes(
              this.options.HTMLAttributes,
              incomingHTMLAttributes,
              {
                class: perLevelClasses[level],
              }
            ),
            0,
          ]
        },
      }),
      ImageExtension.configure({
        allowBase64: true,
      }),
      LinkExtension.configure({
        openOnClick: false, // We handle click via editor, not direct navigation
        autolink: true,
        HTMLAttributes: {
          class: 'text-primary underline hover:text-primary/80',
          target: '_blank', // Default to opening links in a new tab
          rel: 'noopener noreferrer nofollow', // Security and SEO best practices
        },
      }),
      PlaceholderExtension.configure({
        placeholder: placeholder || 'Écrivez votre contenu ici...',
      }),
      TextAlignExtension.configure({
        types: ['heading', 'paragraph'],
      }),
      YoutubeExtension.configure({
        nocookie: true,
        modestBranding: true,
        HTMLAttributes: {
          class: 'aspect-video w-full', // For responsive iframe
        },
      }),
    ],
    content: field.state.value || '',
    editorProps: {
      attributes: {
        class: cn(
          'prose dark:prose-invert max-w-none prose-sm sm:prose-base',
          'min-h-[200px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background',
          'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2',
          'disabled:cursor-not-allowed disabled:opacity-50',
          editorClassName,
          field.state.meta.isTouched &&
            field.state.meta.errors.length > 0 &&
            '!border-destructive'
        ),
      },
    },
    onUpdate: ({ editor: currentEditor }) => {
      const html = currentEditor.getHTML()
      field.handleChange(html === '<p></p>' ? '' : html)
    },
    onBlur: () => {
      field.handleBlur()
    },
  })

  // Fonctions de gestion des dialogues déplacées ici
  const openLinkDialog = useCallback(() => {
    if (!editor) return
    setLinkUrl(editor.getAttributes('link').href || '')
    setIsLinkDialogOpen(true)
  }, [editor])

  const handleSetLink = useCallback(() => {
    if (!editor) return
    if (linkUrl === '') {
      editor.chain().focus().extendMarkRange('link').unsetLink().run()
    } else {
      editor
        .chain()
        .focus()
        .extendMarkRange('link')
        .setLink({ href: linkUrl, target: '_blank' })
        .run()
    }
    setIsLinkDialogOpen(false)
    setLinkUrl('')
  }, [editor, linkUrl])

  const openYoutubeDialog = useCallback(() => {
    if (!editor) return // Vérifier si l'éditeur existe
    setYoutubeUrl('')
    setIsYoutubeDialogOpen(true)
  }, [editor]) // Ajouter editor comme dépendance

  const handleSetYoutubeVideo = useCallback(() => {
    if (!editor || !youtubeUrl) return
    editor.commands.setYoutubeVideo({
      src: youtubeUrl,
    })
    setIsYoutubeDialogOpen(false)
    setYoutubeUrl('')
  }, [editor, youtubeUrl])

  useEffect(() => {
    if (!editor) return

    const formValue = field.state.value || ''
    const editorHTML = editor.getHTML()

    if (
      (editorHTML === '<p></p>' && formValue !== '') ||
      (editorHTML !== '<p></p>' && editorHTML !== formValue)
    ) {
      editor.commands.setContent(formValue, false)
    }
  }, [field.state.value, editor])

  useEffect(() => {
    return () => {
      editor?.destroy()
    }
  }, [editor])

  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  return (
    <div className={cn('space-y-2', className)}>
      <div className="space-y-1.5">
        <Label
          htmlFor={field.name}
          className={cn(hasError && 'text-destructive')}
        >
          {label}
          {required && <span className="text-destructive"> *</span>}
          {tooltip && <SimpleTooltip content={tooltip} />}
        </Label>
        <TiptapToolbar
          editor={editor}
          className={toolbarClassName}
          onOpenLinkDialog={openLinkDialog} // Passer la fonction
          onOpenYoutubeDialog={openYoutubeDialog} // Passer la fonction
        />
        <EditorContent editor={editor} />
        {editor && (
          <BubbleMenu
            editor={editor}
            tippyOptions={{ duration: 100 }}
            className="border-input bg-background flex gap-1 rounded-md border p-1 shadow-md"
          >
            <Button
              type="button"
              variant={editor.isActive('bold') ? 'default' : 'ghost'}
              size="sm"
              onClick={() => editor.chain().focus().toggleBold().run()}
            >
              Bold
            </Button>
            <Button
              type="button"
              variant={editor.isActive('italic') ? 'default' : 'ghost'}
              size="sm"
              onClick={() => editor.chain().focus().toggleItalic().run()}
            >
              Italic
            </Button>
            <Button
              type="button"
              variant={editor.isActive('strike') ? 'default' : 'ghost'}
              size="sm"
              onClick={() => editor.chain().focus().toggleStrike().run()}
            >
              Strike
            </Button>
            <Button
              type="button"
              variant={editor.isActive('link') ? 'default' : 'ghost'}
              size="sm"
              onClick={openLinkDialog} // Maintenant, cela fonctionne
            >
              Link
            </Button>
          </BubbleMenu>
        )}
      </div>
      <FieldErrors meta={field.state.meta} />

      {/* Link Dialog - reste dans RichTextEditorField car les états sont ici */}
      <Dialog open={isLinkDialogOpen} onOpenChange={setIsLinkDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Set Link URL</DialogTitle>
            <DialogDescription>
              Enter the URL for the link. Leave empty to remove the link.
            </DialogDescription>
          </DialogHeader>
          <Input
            value={linkUrl}
            onChange={e => setLinkUrl(e.target.value)}
            placeholder="https://example.com"
            onKeyDown={e => {
              if (e.key === 'Enter') {
                e.preventDefault() // Empêcher la soumission du formulaire parent si le RTE est dans un formulaire
                handleSetLink()
              }
            }}
          />
          <DialogFooter>
            <DialogClose asChild>
              <Button type="button" variant="outline">
                Cancel
              </Button>
            </DialogClose>
            <Button type="button" onClick={handleSetLink}>
              Set Link
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* YouTube Dialog - reste dans RichTextEditorField */}
      <Dialog open={isYoutubeDialogOpen} onOpenChange={setIsYoutubeDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Embed YouTube Video</DialogTitle>
            <DialogDescription>
              Enter the URL of the YouTube video.
            </DialogDescription>
          </DialogHeader>
          <Input
            value={youtubeUrl}
            onChange={e => setYoutubeUrl(e.target.value)}
            placeholder="https://www.youtube.com/watch?v=..."
            onKeyDown={e => {
              if (e.key === 'Enter') {
                e.preventDefault()
                handleSetYoutubeVideo()
              }
            }}
          />
          <DialogFooter>
            <DialogClose asChild>
              <Button type="button" variant="outline">
                Cancel
              </Button>
            </DialogClose>
            <Button type="button" onClick={handleSetYoutubeVideo}>
              Embed Video
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
