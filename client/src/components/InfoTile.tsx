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
      className={`flex w-full flex-col items-center justify-center rounded-lg bg-white/50 p-2 text-center shadow-xs`}
    >
      <div
        className={`flex items-center justify-center gap-1 text-neutral-700 ${labelSize}`}
      >
        {icon}
        {label}
      </div>
      <div className={`mt-1 leading-tight font-semibold ${contentSize}`}>
        {children}
      </div>
    </div>
  )
}
