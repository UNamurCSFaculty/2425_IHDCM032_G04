export const InfoTile: React.FC<{
  icon?: React.ReactNode
  label: string
  children: React.ReactNode
  size?: 'sm' | 'lg'
}> = ({ icon, label, children, size = 'sm' }) => {
  const labelSize = size === 'sm' ? 'text-xs' : 'text-lg'
  const contentSize = size === 'sm' ? 'text-sm' : 'text-xl'

  return (
    <div
      className={`w-full rounded-lg bg-white/50  shadow-xs  p-2 text-center flex items-center justify-center flex-col`}
    >
      <div
        className={`flex items-center justify-center gap-1 text-muted-foreground ${labelSize}`}
      >
        {icon}
        {label}
      </div>
      <div className={`font-semibold mt-1 leading-tight ${contentSize}`}>
        {children}
      </div>
    </div>
  )
}
